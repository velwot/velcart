package com.example

import com.example.domain.IngredientAnalysisService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IngredientAnalysisServiceTest {

    private lateinit var service: IngredientAnalysisService

    @Before
    fun setUp() {
        service = IngredientAnalysisService()
    }

    @Test
    fun testCleanProductScoring() {
        // Formulation with only clean whole ingredients
        val cleanIngredients = "Rolled Oats, Almonds, Cashews, Desi Ghee, Pure Honey, Cardamom, Turmeric"
        val analysis = service.analyze(cleanIngredients, "Oat Granola")

        assertTrue("Clean product score must be >= 80, got ${analysis.score}", analysis.score >= 80)
        assertEquals("CLEAN", analysis.statusLabel)
        assertEquals(0, analysis.flaggedIngredients.size)
        assertTrue(analysis.keyConcerns.contains("CLEAN FORMULATION") || analysis.keyConcerns.contains("WHOLE INGREDIENTS"))
    }

    @Test
    fun testUltraProcessedProductScoring() {
        // Formulation with Palm Oil, TBHQ, Flavour Enhancers, Artificial Colour
        val ultraProcessed = "Refined Wheat Flour (Maida), Palm Oil, Iodised Salt, Flavour Enhancers (INS 627, INS 631), Caramel Colour (INS 150d), Antioxidant (INS 319)"
        val analysis = service.analyze(ultraProcessed, "Instant Noodles")

        // Penalties: Palm oil (12), Maida (6), INS 627 (12), INS 631 (12), INS 150d (12), INS 319 (12)
        // Score: 100 - (12 + 6 + 12 + 12 + 12 + 12) = 100 - 66 = 34
        assertTrue("Ultra-processed product must receive AVOID status, got score ${analysis.score}", analysis.score < 50)
        assertEquals("AVOID", analysis.statusLabel)
        assertTrue("Must flag high concern items", analysis.flaggedIngredients.size >= 4)
        assertTrue(analysis.keyConcerns.contains("PALM OIL"))
        assertTrue(analysis.keyConcerns.contains("FLAVOUR ENHANCERS"))
        assertTrue(analysis.keyConcerns.contains("ARTIFICIAL COLOUR") || analysis.keyConcerns.contains("SYNTHETIC PRESERVATIVE"))
    }

    @Test
    fun testScoresAreDynamicNotStatic() {
        val prod1 = "Whole Wheat Flour (Atta), Water, Desi Ghee, Salt"
        val prod2 = "Refined Wheat Flour (Maida), Sugar, Edible Vegetable Oil"
        val prod3 = "Palm Oil, High Fructose Corn Syrup, Caramel Colour (INS 150d), TBHQ, Flavour Enhancer (635)"

        val score1 = service.analyze(prod1).score
        val score2 = service.analyze(prod2).score
        val score3 = service.analyze(prod3).score

        assertNotEquals("Scores must not be static between clean and moderate formulations", score1, score2)
        assertNotEquals("Scores must not be static between moderate and ultra-processed formulations", score2, score3)
        assertTrue("prod1 score ($score1) must be higher than prod2 score ($score2)", score1 > score2)
        assertTrue("prod2 score ($score2) must be higher than prod3 score ($score3)", score2 > score3)
    }

    @Test
    fun testInsNumbersClassifiedAccurately() {
        // INS 330 (Citric acid) and INS 500 (Baking soda) are safe; INS 319 (TBHQ) and INS 102 (Tartrazine) are high concern
        val raw = "INS 330, INS 500, INS 319, INS 102"
        val analysis = service.analyze(raw)

        val ins330 = analysis.allIngredients.find { it.name.contains("330") }
        val ins500 = analysis.allIngredients.find { it.name.contains("500") }
        val ins319 = analysis.allIngredients.find { it.name.contains("319") }
        val ins102 = analysis.allIngredients.find { it.name.contains("102") }

        assertNotNull(ins330)
        assertNotNull(ins500)
        assertNotNull(ins319)
        assertNotNull(ins102)

        assertFalse("INS 330 (Citric acid) should not be flagged as high concern", ins330!!.isFlagged)
        assertFalse("INS 500 (Baking soda) should not be flagged as high concern", ins500!!.isFlagged)
        assertTrue("INS 319 (TBHQ) must be flagged", ins319!!.isFlagged)
        assertTrue("INS 102 (Tartrazine) must be flagged", ins102!!.isFlagged)
    }

    @Test
    fun testUnknownIngredientsDoNotPenalizeScore() {
        val knownClean = "Rolled Oats, Water"
        val scoreKnown = service.analyze(knownClean).score

        // Adding an unknown ingredient should not apply a penalty (per AGENTS.md rule: unknown is not harmful)
        val withUnknown = "Rolled Oats, Water, XylotriniumXYZ"
        val analysisWithUnknown = service.analyze(withUnknown)

        assertEquals("Unknown ingredient should not reduce PureScan score", scoreKnown, analysisWithUnknown.score)
        assertTrue("Unknown ingredient must appear in unknownIngredients list",
            analysisWithUnknown.unknownIngredients.any { it.contains("XylotriniumXYZ", ignoreCase = true) }
        )
    }

    @Test
    fun testCompoundIngredientParsing() {
        val compound = "Acidity Regulators (501(i) & 500(i)), Thickeners (412 & 508), Mineral (Calcium Carbonate)"
        val analysis = service.analyze(compound)

        assertEquals(5, analysis.totalIngredients)
        assertTrue(analysis.allIngredients.any { it.name.contains("501") })
        assertTrue(analysis.allIngredients.any { it.name.contains("500") })
        assertTrue(analysis.allIngredients.any { it.name.contains("412") })
        assertTrue(analysis.allIngredients.any { it.name.contains("508") })
        assertTrue(analysis.allIngredients.any { it.name.contains("Calcium Carbonate", ignoreCase = true) })
    }

    @Test
    fun testFlaggedIngredientsIncludeHealthProblems() {
        val raw = "Palm Oil, Caramel Colour (INS 150d), Antioxidant (INS 319), Refined Wheat Flour (Maida)"
        val analysis = service.analyze(raw)

        assertEquals(4, analysis.flaggedIngredients.size)
        analysis.flaggedIngredients.forEach { item ->
            assertTrue(
                "Flagged ingredient ${item.canonicalName} must have health problems specified",
                item.healthProblems.isNotBlank()
            )
            assertTrue(
                "Flagged ingredient ${item.canonicalName} must have health risks listed",
                item.healthRisks.isNotEmpty()
            )
        }

        val palmOil = analysis.flaggedIngredients.find { it.canonicalName.contains("Palm Oil", ignoreCase = true) }
        assertNotNull(palmOil)
        assertTrue(palmOil!!.healthProblems.contains("cholesterol", ignoreCase = true) || palmOil.healthProblems.contains("cardiovascular", ignoreCase = true))

        val caramelColour = analysis.flaggedIngredients.find { it.canonicalName.contains("Caramel Colour", ignoreCase = true) }
        assertNotNull(caramelColour)
        assertTrue(caramelColour!!.healthProblems.contains("4-MEI", ignoreCase = true) || caramelColour.healthProblems.contains("carcinogen", ignoreCase = true))
    }
}
