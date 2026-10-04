package com.example.domain

import com.example.model.EvaluatedIngredient
import com.example.model.ProductAnalysis

class IngredientAnalysisService {

    data class IngredientKnowledge(
        val canonicalName: String,
        val aliases: List<String>,
        val concernLevel: String, // "none", "low", "moderate", "high"
        val itemScore: Int,
        val isFlagged: Boolean,
        val category: String,
        val explanation: String,
        val evidenceSummary: String = "",
        val sources: List<String> = emptyList(),
        val healthProblems: String = "",
        val healthRisks: List<String> = emptyList()
    )

    // Configurable penalty points matching PureScan Scoring Specification (docs/scoring_spec.md)
    companion object {
        const val PENALTY_NONE = 0
        const val PENALTY_LOW = 2
        const val PENALTY_MODERATE = 6
        const val PENALTY_HIGH = 12
    }

    private val catalog = listOf(
        // === HIGH CONCERN OILS, TRANS FATS & INDUSTRIAL SHORTENINGS ===
        IngredientKnowledge(
            canonicalName = "Palm Oil",
            aliases = listOf("palm oil", "palmolein", "refined palm oil", "fractionated palm oil", "palm kernel oil", "palmolein oil", "palm olein"),
            concernLevel = "high",
            itemScore = 20,
            isFlagged = true,
            category = "Tropical / Industrial Oil",
            explanation = "Heavily refined industrial frying oil rich in saturated palmitic acid and heat-induced oxidized lipid byproducts.",
            evidenceSummary = "High palmitic acid content; industrial high-heat deodorization produces MCPD and glycidyl esters.",
            healthProblems = "Elevated LDL ('bad') cholesterol, arterial plaque deposition, increased risk of coronary heart disease, visceral liver fat accumulation, and exposure to toxic heat-induced 3-MCPD esters.",
            healthRisks = listOf("Coronary Heart Disease", "Elevated LDL Cholesterol", "Arterial Plaque", "3-MCPD Toxic Esters", "Fatty Liver")
        ),
        IngredientKnowledge(
            canonicalName = "Hydrogenated Vegetable Oil (Trans Fat)",
            aliases = listOf("hydrogenated vegetable oil", "partially hydrogenated oil", "partially hydrogenated vegetable oil", "vanaspati", "margarine", "vegetable shortening", "interesterified fat", "trans fat"),
            concernLevel = "high",
            itemScore = 12,
            isFlagged = true,
            category = "Industrial Trans Fat",
            explanation = "Industrial hydrogenated fat containing harmful trans fatty acids that raise LDL cholesterol and promote cardiovascular disease.",
            evidenceSummary = "Trans fatty acids cause systemic endothelial dysfunction and raise coronary heart disease risk.",
            healthProblems = "Severe coronary artery disease, systemic inflammation (hs-CRP), endothelial damage, reduction of protective HDL cholesterol, and elevated stroke risk.",
            healthRisks = listOf("Coronary Artery Disease", "Stroke Risk", "Systemic Endothelial Damage", "Depressed HDL Cholesterol")
        ),
        IngredientKnowledge(
            canonicalName = "Cottonseed Oil",
            aliases = listOf("cottonseed oil", "refined cottonseed oil"),
            concernLevel = "high",
            itemScore = 22,
            isFlagged = true,
            category = "Industrial Seed Oil",
            explanation = "Refined industrial oil derived from a non-food crop typically treated with heavy pesticides; very high in inflammatory omega-6.",
            evidenceSummary = "High omega-6 linoleic acid ratio, potential gossypol pesticide residues.",
            healthProblems = "Chronic inflammatory cascade driven by severe omega-6 to omega-3 imbalance, cellular lipid peroxidation, and potential trace agricultural pesticide residues.",
            healthRisks = listOf("Pro-inflammatory Cascade", "Cellular Lipid Peroxidation", "Pesticide Residues")
        ),
        IngredientKnowledge(
            canonicalName = "High Fructose Corn Syrup (HFCS)",
            aliases = listOf("high fructose corn syrup", "hfcs", "corn syrup solids", "glucose-fructose syrup"),
            concernLevel = "high",
            itemScore = 20,
            isFlagged = true,
            category = "Refined Sweetener",
            explanation = "Processed sweetener metabolized exclusively by the liver, accelerating de novo lipogenesis and visceral fat accumulation.",
            evidenceSummary = "Directly linked to non-alcoholic fatty liver disease (NAFLD) and insulin resistance.",
            healthProblems = "Non-alcoholic fatty liver disease (NAFLD), severe peripheral insulin resistance, rapid abdominal visceral fat storage, elevated uric acid, and Type-2 diabetes.",
            healthRisks = listOf("Fatty Liver Disease (NAFLD)", "Type-2 Diabetes", "Insulin Resistance", "Visceral Adiposity", "High Uric Acid")
        ),
        IngredientKnowledge(
            canonicalName = "Maltodextrin",
            aliases = listOf("maltodextrin"),
            concernLevel = "high",
            itemScore = 25,
            isFlagged = true,
            category = "Refined Carbohydrate",
            explanation = "Ultra-processed starch with a glycemic index (110–135) higher than table sugar, triggering rapid postprandial insulin spikes.",
            evidenceSummary = "Extremely high glycemic index (GI 110-135); rapidly disrupts gut barrier mucosal layer.",
            healthProblems = "Severe rapid blood glucose surges followed by hypoglycemic crashes, erosion of intestinal protective mucosal barrier, and suppression of beneficial gut microbiota.",
            healthRisks = listOf("Extreme Blood Sugar Spikes", "Insulin Surges", "Gut Mucosal Erosion", "Dysbiosis")
        ),

        // === SYNTHETIC COLOURS & DYES (HIGH CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Caramel Colour (INS 150d)",
            aliases = listOf("caramel colour", "caramel color", "caramel color iv", "ins 150d", "150d", "e150d", "ammonia sulphite caramel", "sulphite ammonia caramel"),
            concernLevel = "high",
            itemScore = 22,
            isFlagged = true,
            category = "Food Dye",
            explanation = "Synthetic dark colour synthesized with ammonia and sulphites; contains 4-MEI, a compound classified as potentially carcinogenic.",
            evidenceSummary = "IARC classifies 4-methylimidazole (4-MEI) as a possible human carcinogen (Group 2B).",
            healthProblems = "Dietary exposure to 4-methylimidazole (4-MEI), identified by WHO IARC as a Group 2B possible human carcinogen; cellular oxidative stress and immune modulation.",
            healthRisks = listOf("Possible Carcinogen (4-MEI)", "Immune Cell Alterations", "Cellular Oxidative Stress")
        ),
        IngredientKnowledge(
            canonicalName = "Tartrazine (INS 102 / Yellow 5)",
            aliases = listOf("tartrazine", "ins 102", "102", "e102", "yellow 5", "fd&c yellow no. 5"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Azo Dye",
            explanation = "Petroleum-derived synthetic azo dye associated with hyperactivity in children, allergic reactions, and urticaria.",
            evidenceSummary = "The Southampton study linked azo food dyes with behavioral hyperactivity in children.",
            healthProblems = "Childhood neuro-behavioral hyperactivity, chronic urticaria (itchy hives), severe asthma exacerbation, and genotoxic damage in mammalian colonic tissue.",
            healthRisks = listOf("Childhood Hyperactivity (ADHD-like)", "Chronic Hives (Urticaria)", "Asthma Aggravation", "Colonic DNA Damage")
        ),
        IngredientKnowledge(
            canonicalName = "Sunset Yellow (INS 110 / Yellow 6)",
            aliases = listOf("sunset yellow", "sunset yellow fcf", "ins 110", "110", "e110", "yellow 6", "fd&c yellow no. 6"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Azo Dye",
            explanation = "Synthetic coal-tar dye banned or restricted in several European countries due to allergic sensitivities and hyperactivity concerns.",
            evidenceSummary = "Synthetic azo dye requiring warning label in the European Union.",
            healthProblems = "Immune hypersensitivity reactions, gastric and intestinal irritation, behavioral restlessness, and restricted status in the EU.",
            healthRisks = listOf("Immune Hypersensitivity", "Gastric Irritation", "EU Warning Restriction", "Hyperactivity")
        ),
        IngredientKnowledge(
            canonicalName = "Allura Red (INS 129 / Red 40)",
            aliases = listOf("allura red", "allura red ac", "ins 129", "129", "e129", "red 40", "fd&c red no. 40"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Azo Dye",
            explanation = "Synthetic petroleum dye associated with gut inflammation and immune hyper-responsiveness in animal studies.",
            evidenceSummary = "May trigger histamine release and aggravate intestinal inflammation.",
            healthProblems = "Intestinal mucosal barrier disruption, histamine-mediated allergic episodes, dermatitis, and aggravation of chronic colitis.",
            healthRisks = listOf("Gut Mucosal Inflammation", "Histamine Allergic Episodes", "Dermatitis", "Colitis Flare-ups")
        ),
        IngredientKnowledge(
            canonicalName = "Brilliant Blue (INS 133 / Blue 1)",
            aliases = listOf("brilliant blue", "brilliant blue fcf", "ins 133", "133", "e133", "blue 1", "fd&c blue no. 1"),
            concernLevel = "high",
            itemScore = 20,
            isFlagged = true,
            category = "Synthetic Dye",
            explanation = "Synthetic triphenylmethane dye poorly absorbed by the gastrointestinal tract with potential neurotoxicological concerns.",
            evidenceSummary = "Synthetic dye with reports of hypersensitivity reactions.",
            healthProblems = "Hypersensitivity allergic reactions, cellular accumulation due to sluggish renal elimination, and cross-reactivity with other food dyes.",
            healthRisks = listOf("Hypersensitivity Reactions", "Sluggish Renal Elimination", "Cellular Accumulation")
        ),
        IngredientKnowledge(
            canonicalName = "Carmoisine (INS 122)",
            aliases = listOf("carmoisine", "azorubine", "ins 122", "122", "e122"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Azo Dye",
            explanation = "Synthetic red food dye associated with allergic intolerance, especially in individuals sensitive to salicylates.",
            evidenceSummary = "Azo dye restricted in the EU and banned in several jurisdictions.",
            healthProblems = "Severe allergic intolerance (especially in aspirin/salicylate sensitive persons), facial swelling, skin eruptions, and banned in multiple countries.",
            healthRisks = listOf("Salicylate Cross-Sensitivity", "Facial Swelling & Rashes", "International Prohibitions")
        ),
        IngredientKnowledge(
            canonicalName = "Titanium Dioxide (INS 171)",
            aliases = listOf("titanium dioxide", "ins 171", "171", "e171"),
            concernLevel = "high",
            itemScore = 15,
            isFlagged = true,
            category = "Mineral Whitener",
            explanation = "Whitening pigment containing nanoparticles banned as a food additive in the European Union due to genotoxicity concerns.",
            evidenceSummary = "European Food Safety Authority (EFSA) concluded it can no longer be considered safe due to DNA accumulation.",
            healthProblems = "Nanoparticle tissue bioaccumulation, genotoxicity (cellular DNA strand breaks), and complete prohibition across the European Union.",
            healthRisks = listOf("Genotoxicity (DNA Damage)", "Banned by EFSA in EU", "Nanoparticle Accumulation")
        ),

        // === SYNTHETIC FLAVOUR ENHANCERS (HIGH / MODERATE CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Disodium Guanylate (INS 627)",
            aliases = listOf("disodium guanylate", "ins 627", "627", "e627", "sodium guanylate"),
            concernLevel = "high",
            itemScore = 25,
            isFlagged = true,
            category = "Flavor Enhancer",
            explanation = "Synthetic ribonucleotide taste enhancer that over-stimulates taste receptors to induce cravings for ultra-processed foods.",
            evidenceSummary = "Synergistic glutamate-receptor agonist; metabolized into uric acid purines.",
            healthProblems = "Neuroreceptor habituation driving intense cravings and overeating; purine breakdown into uric acid, exacerbating painful gout flares and kidney stones.",
            healthRisks = listOf("Compulsive Craving & Overeating", "Gout Flares (High Uric Acid)", "Kidney Uric Acid Burden")
        ),
        IngredientKnowledge(
            canonicalName = "Disodium Inosinate (INS 631)",
            aliases = listOf("disodium inosinate", "ins 631", "631", "e631", "sodium inosinate"),
            concernLevel = "high",
            itemScore = 25,
            isFlagged = true,
            category = "Flavor Enhancer",
            explanation = "Purine-based nucleotide flavor enhancer used in tandem with MSG to drive hyper-palatability.",
            evidenceSummary = "Synthetically heightened savoriness; metabolized into purines, contraindicated in hyperuricemia.",
            healthProblems = "Purine-induced hyperuricemia, gout attacks, transient headaches, and artificial taste distortion overriding natural satiety signaling.",
            healthRisks = listOf("Hyperuricemia (Gout)", "Satiety Override", "Transient Headaches")
        ),
        IngredientKnowledge(
            canonicalName = "Flavour Enhancer (INS 635)",
            aliases = listOf("ins 635", "635", "e635", "disodium 5'-ribonucleotides", "disodium ribonucleotides"),
            concernLevel = "high",
            itemScore = 25,
            isFlagged = true,
            category = "Flavor Enhancer",
            explanation = "Potent combination of disodium inosinate and guanylate that artificially intensifies savory perception by up to 500%.",
            evidenceSummary = "Triggers intense savoriness to mask absence of natural whole-food ingredients.",
            healthProblems = "Acute cutaneous itchiness, periorbital swelling, gout aggravation, and potent dopamine-mediated appetite overstimulation.",
            healthRisks = listOf("Itching & Skin Rash", "Gout Attacks", "Extreme Palatability Cravings")
        ),
        IngredientKnowledge(
            canonicalName = "Monosodium Glutamate (MSG / INS 621)",
            aliases = listOf("monosodium glutamate", "msg", "ins 621", "621", "e621"),
            concernLevel = "moderate",
            itemScore = 40,
            isFlagged = true,
            category = "Flavor Enhancer",
            explanation = "Concentrated sodium salt of glutamic acid that triggers glutamate taste receptors; may provoke headaches in sensitive individuals.",
            evidenceSummary = "Excitatory neurotransmitter precursor; stimulates appetite for high-calorie snacks.",
            healthProblems = "Glutamate sensitivity symptoms (headaches, facial pressure, sweating, chest tightness), transient blood pressure spikes, and appetite hyper-stimulation.",
            healthRisks = listOf("Glutamate Sensitivity Headaches", "Facial Pressure & Flushing", "Appetite Over-stimulation")
        ),
        IngredientKnowledge(
            canonicalName = "Hydrolyzed Vegetable Protein (HVP)",
            aliases = listOf("hydrolyzed peanut protein", "hydrolyzed vegetable protein", "hvp", "hydrolyzed groundnut protein", "hydrolyzed soy protein", "hydrolysed vegetable protein", "hydrolysed groundnut protein"),
            concernLevel = "moderate",
            itemScore = 38,
            isFlagged = true,
            category = "Processed Protein",
            explanation = "Acid-hydrolyzed plant protein rich in concentrated free glutamates designed to simulate meaty broth flavor in packaged snacks.",
            evidenceSummary = "Chemical processing byproduct that acts as an unlabelled source of free glutamates.",
            healthProblems = "Unlabelled high concentration of free excitotoxic glutamates, headache triggers, and potential processing contaminants like 3-MCPD.",
            healthRisks = listOf("High Free Glutamates", "Migraine Trigger", "3-MCPD Processing Contaminants")
        ),
        IngredientKnowledge(
            canonicalName = "Yeast Extract",
            aliases = listOf("yeast extract", "autolyzed yeast", "autolyzed yeast extract"),
            concernLevel = "moderate",
            itemScore = 42,
            isFlagged = true,
            category = "Flavor Enhancer",
            explanation = "Processed cellular extract containing natural free glutamates used to heighten savoriness in commercial formulations.",
            evidenceSummary = "Concentrated glutamate source used as a clean-label alternative to MSG.",
            healthProblems = "Migraine headaches in glutamate-sensitive individuals, abdominal bloating, and artificial savoriness masking low-nutrient base grains.",
            healthRisks = listOf("Migraine Trigger", "Glutamate Load", "Abdominal Discomfort")
        ),

        // === SYNTHETIC ANTIOXIDANTS & PRESERVATIVES (HIGH CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Antioxidant (TBHQ / INS 319)",
            aliases = listOf("antioxidant (ins 319)", "ins 319", "319", "e319", "tbhq", "tertiary butylhydroquinone"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Preservative",
            explanation = "Petroleum-derived synthetic antioxidant additive linked to immune alterations and cellular oxidative stress in animal models.",
            evidenceSummary = "NTP and toxicology studies associate chronic high exposure with immune cell alterations.",
            healthProblems = "Immune system impairment (suppresses T-helper cell responses), liver enzyme alterations, cellular DNA damage, and neurotoxic symptoms in animal toxicology assays.",
            healthRisks = listOf("Immune Dysregulation", "DNA Strand Damage", "Hepatic Enzyme Alteration", "Neurotoxic Potential")
        ),
        IngredientKnowledge(
            canonicalName = "Butylated Hydroxyanisole (BHA / INS 320)",
            aliases = listOf("ins 320", "320", "e320", "bha", "butylated hydroxyanisole"),
            concernLevel = "high",
            itemScore = 15,
            isFlagged = true,
            category = "Synthetic Preservative",
            explanation = "Synthetic phenolic antioxidant classified by the US National Toxicology Program as reasonably anticipated to be a human carcinogen.",
            evidenceSummary = "Endocrine disrupting properties and carcinogenic potential in animal studies.",
            healthProblems = "Endocrine disruption (disrupts thyroid and estrogen signaling), classified as a reasonably anticipated human carcinogen by the US NTP, and liver stress.",
            healthRisks = listOf("Endocrine Disruption", "Reasonably Anticipated Carcinogen", "Thyroid Interference")
        ),
        IngredientKnowledge(
            canonicalName = "Butylated Hydroxytoluene (BHT / INS 321)",
            aliases = listOf("ins 321", "321", "e321", "bht", "butylated hydroxytoluene"),
            concernLevel = "high",
            itemScore = 18,
            isFlagged = true,
            category = "Synthetic Preservative",
            explanation = "Petroleum-based synthetic preservative linked to hepatic enzyme alterations and thyroid hormone disruption in animal research.",
            evidenceSummary = "Bioaccumulative synthetic additive with potential endocrine disrupting effects.",
            healthProblems = "Hepatic bioaccumulation, thyroid hormone imbalance, lung tissue toxic metabolites in animal trials, and suspected endocrine interference.",
            healthRisks = listOf("Hepatic Bioaccumulation", "Thyroid Disruption", "Endocrine Modulation")
        ),
        IngredientKnowledge(
            canonicalName = "Sodium Benzoate (INS 211)",
            aliases = listOf("sodium benzoate", "ins 211", "211", "e211", "benzoate"),
            concernLevel = "moderate",
            itemScore = 36,
            isFlagged = true,
            category = "Chemical Preservative",
            explanation = "Synthetic chemical antimicrobial preservative; when combined with ascorbic acid (vitamin C), it can form trace carcinogenic benzene.",
            evidenceSummary = "Benzene formation risk in acidic formulations containing ascorbic acid.",
            healthProblems = "Formation of carcinogenic benzene when combined with vitamin C (ascorbic acid), aggravated childhood hyperactivity, and asthmatic bronchospasms.",
            healthRisks = listOf("Benzene Formation Risk", "Asthma Bronchospasms", "Childhood Hyperactivity")
        ),
        IngredientKnowledge(
            canonicalName = "Sodium Metabisulphite (INS 223)",
            aliases = listOf("sodium metabisulphite", "sodium metabisulfite", "ins 223", "223", "e223", "potassium metabisulphite", "ins 224", "sulphur dioxide", "ins 220"),
            concernLevel = "moderate",
            itemScore = 38,
            isFlagged = true,
            category = "Sulphite Preservative",
            explanation = "Inorganic sulphite preservative known to trigger acute respiratory bronchospasms and asthmatic reactions in sensitive individuals.",
            evidenceSummary = "Recognized potent allergen requiring mandatory declaration in food regulations.",
            healthProblems = "Severe acute asthma attacks, bronchial constriction, flushing, abdominal cramping, and irreversible degradation of dietary vitamin B1 (thiamine).",
            healthRisks = listOf("Severe Asthmatic Attacks", "Bronchial Constriction", "Thiamine (Vit B1) Destruction")
        ),

        // === ARTIFICIAL SWEETENERS (HIGH CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Aspartame (INS 951)",
            aliases = listOf("aspartame", "ins 951", "951", "e951"),
            concernLevel = "high",
            itemScore = 20,
            isFlagged = true,
            category = "Artificial Sweetener",
            explanation = "Synthetic dipeptide artificial sweetener classified by the WHO IARC as 'possibly carcinogenic to humans' (Group 2B).",
            evidenceSummary = "WHO IARC Group 2B classification; breaks down into phenylalanine, aspartic acid, and methanol.",
            healthProblems = "Classified as a Group 2B possible human carcinogen by the WHO IARC; breaks down into neurotoxic formaldehyde and aspartic acid; headaches and dizziness.",
            healthRisks = listOf("IARC Group 2B Carcinogen", "Formaldehyde Breakdown", "Headaches & Dizziness", "Microbiome Imbalance")
        ),
        IngredientKnowledge(
            canonicalName = "Acesulfame Potassium (INS 950)",
            aliases = listOf("acesulfame potassium", "acesulfame k", "ins 950", "950", "e950", "ace-k"),
            concernLevel = "high",
            itemScore = 22,
            isFlagged = true,
            category = "Artificial Sweetener",
            explanation = "Calorie-free artificial chemical sweetener shown in recent microbiome studies to alter gut bacterial diversity.",
            evidenceSummary = "Calorie-free chemical sweetener; potential microbiome composition alteration.",
            healthProblems = "Disruption of beneficial gut microbiome diversity, impaired glycemic insulin sensitivity, and persistent sweet palate conditioning.",
            healthRisks = listOf("Gut Microbiome Dysbiosis", "Impaired Insulin Sensitivity", "Sweet Cravings")
        ),
        IngredientKnowledge(
            canonicalName = "Sucralose (INS 955)",
            aliases = listOf("sucralose", "ins 955", "955", "e955"),
            concernLevel = "high",
            itemScore = 25,
            isFlagged = true,
            category = "Artificial Sweetener",
            explanation = "Chlorinated artificial sweetener that can reduce beneficial gut bifidobacteria and impairs glycemic insulin sensitivity.",
            evidenceSummary = "Organochlorine sweetener; decreases beneficial gut microbiota counts in clinical studies.",
            healthProblems = "Up to 50% depletion of protective gut Bifidobacteria and Lactobacilli, impaired postprandial glucose control, and formation of toxic chloropropanols under baking heat.",
            healthRisks = listOf("Gut Bifidobacteria Depletion", "Impaired Glucose Response", "Chloropropanols Under Heat")
        ),

        // === REFINED GRAINS, FLOURS & REFINED SUGAR (MODERATE CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Refined Wheat Flour (Maida)",
            aliases = listOf("refined wheat flour", "maida", "all purpose flour", "white flour", "bleached flour", "refined flour"),
            concernLevel = "moderate",
            itemScore = 40,
            isFlagged = true,
            category = "Refined Grain",
            explanation = "Endosperm flour stripped of bran and germ fibers; rapidly metabolized into glucose, prompting blood sugar spikes.",
            evidenceSummary = "Refining removes 80% of dietary fiber, magnesium, and B vitamins; rapid starch digestion.",
            healthProblems = "Rapid postprandial blood sugar spikes followed by insulin crashes, high risk of insulin resistance, chronic constipation due to lack of fiber, and visceral fat gain.",
            healthRisks = listOf("Blood Sugar Spikes", "Insulin Resistance", "Chronic Constipation", "Visceral Adiposity")
        ),
        IngredientKnowledge(
            canonicalName = "Sugar",
            aliases = listOf("sugar", "cane sugar", "sucrose", "dextrose", "invert sugar", "invert syrup", "liquid glucose", "glucose syrup", "corn syrup"),
            concernLevel = "moderate",
            itemScore = 42,
            isFlagged = true,
            category = "Refined Sweetener",
            explanation = "Refined sucrose and glucose syrup promote rapid insulin surges, tooth decay, and feed dysbiotic gut microbiota.",
            evidenceSummary = "High dietary intake of added simple sugars correlates with systemic metabolic inflammation.",
            healthProblems = "Systemic low-grade metabolic inflammation, dental caries, non-alcoholic fatty liver risk, elevated triglycerides, and leptin resistance (suppressed satiety).",
            healthRisks = listOf("Systemic Inflammation", "Dental Caries", "Elevated Triglycerides", "Fatty Liver Risk", "Leptin Resistance")
        ),
        IngredientKnowledge(
            canonicalName = "Corn Flour (Corn Starch)",
            aliases = listOf("corn flour", "cornflour", "maize starch", "corn starch", "modified corn starch"),
            concernLevel = "moderate",
            itemScore = 48,
            isFlagged = false,
            category = "Refined Starch",
            explanation = "Refined cereal starch stripped of protein and dietary fiber; rapidly digested into glucose.",
            evidenceSummary = "Refined carbohydrate with high glycemic index.",
            healthProblems = "Elevated glycemic load; rapid digestion into simple glucose.",
            healthRisks = listOf("High Glycemic Load")
        ),
        IngredientKnowledge(
            canonicalName = "Edible Vegetable Oil (Seed Oil)",
            aliases = listOf("edible vegetable oil", "vegetable oil", "canola oil", "soybean oil", "sunflower oil", "refined sunflower oil", "corn oil"),
            concernLevel = "moderate",
            itemScore = 45,
            isFlagged = true,
            category = "Refined Seed Oil",
            explanation = "Industrial seed oil high in unstable omega-6 polyunsaturated fatty acids, prone to oxidation during high-heat cooking.",
            evidenceSummary = "High ratio of linoleic acid (omega-6) promotes pro-inflammatory lipid mediators when oxidised.",
            healthProblems = "Elevated pro-inflammatory omega-6 arachidonic acid cascade, cellular membrane lipid oxidation, and formation of toxic aldehydes when reheated.",
            healthRisks = listOf("Inflammatory Omega-6 Cascade", "Oxidized Lipid Byproducts", "Toxic Aldehyde Formation")
        ),
        IngredientKnowledge(
            canonicalName = "Rice Bran Oil",
            aliases = listOf("rice bran oil", "refined rice bran oil"),
            concernLevel = "moderate",
            itemScore = 48,
            isFlagged = false,
            category = "Seed / Grain Oil",
            explanation = "Solvent-extracted high-smoke point frying oil; contains gamma-oryzanol antioxidants but undergoes intensive industrial chemical refining.",
            evidenceSummary = "Refined solvent extraction; high in monounsaturated and polyunsaturated fatty acids.",
            healthProblems = "Heavy industrial chemical solvent extraction (hexane) and high-heat deodorization.",
            healthRisks = listOf("Industrial Solvent Refining")
        ),

        // === PHOSPHATE ADDITIVES & HUMECTANTS (MODERATE CONCERN) ===
        IngredientKnowledge(
            canonicalName = "Humectant (Phosphates / INS 451)",
            aliases = listOf("humectant", "ins 451", "451", "e451", "ins 451(i)", "451(i)", "sodium tripolyphosphate", "polyphosphates", "ins 452", "452", "ins 450", "450", "diphosphates"),
            concernLevel = "moderate",
            itemScore = 45,
            isFlagged = true,
            category = "Phosphate Additive",
            explanation = "Synthetic polyphosphate salts used to bind water; excessive dietary inorganic phosphates are linked to accelerated arterial and kidney stress.",
            evidenceSummary = "Inorganic phosphates are absorbed at near 100% efficiency, impacting vascular calcification.",
            healthProblems = "Accelerated vascular calcification, arterial wall stiffening, bone mineral density leaching, and long-term renal filtration strain.",
            healthRisks = listOf("Arterial Wall Calcification", "Kidney Strain", "Bone Mineral Depletion")
        ),
        IngredientKnowledge(
            canonicalName = "Carrageenan (INS 407)",
            aliases = listOf("carrageenan", "ins 407", "407", "e407"),
            concernLevel = "moderate",
            itemScore = 42,
            isFlagged = true,
            category = "Vegetable Gum",
            explanation = "Seaweed-derived sulfated polysaccharide stabilizer linked in gastroenterological research to intestinal mucosal inflammation.",
            evidenceSummary = "Associated with disrupted intestinal tight junctions and epithelial inflammation.",
            healthProblems = "Ulceration of intestinal lining, increased gut permeability ('leaky gut'), flares in inflammatory bowel disease (IBD / ulcerative colitis), and glucose intolerance.",
            healthRisks = listOf("Intestinal Ulceration", "IBD / Colitis Flares", "Leaky Gut Barrier Disruption")
        ),

        // === LOW CONCERN INGREDIENTS & MILD ADDITIVES ===
        IngredientKnowledge(
            canonicalName = "Acidity Regulator (Citric Acid / Carbonates)",
            aliases = listOf("acidity regulator", "acidity regulators", "ins 330", "330", "e330", "citric acid", "ins 500", "500", "e500", "ins 500(i)", "ins 500(ii)", "sodium bicarbonate", "baking soda", "ins 501", "501", "e501", "ins 501(i)", "potassium carbonate", "ins 296", "296", "malic acid", "ins 334", "334", "tartaric acid"),
            concernLevel = "none",
            itemScore = 75,
            isFlagged = false,
            category = "Mineral & Organic Acid",
            explanation = "Food-grade organic fruit acids (citric, malic) and mineral carbonates (baking soda) used to stabilize dough texture and pH.",
            evidenceSummary = "Standard non-toxic metabolic intermediates with comprehensive safety profiles.",
            healthProblems = "Generally non-toxic; excessive acid contact may mildly contribute to tooth enamel erosion.",
            healthRisks = listOf("Mild Dental Enamel Acidity (if unbuffered)")
        ),
        IngredientKnowledge(
            canonicalName = "Thickener (Guar Gum / INS 412)",
            aliases = listOf("thickener", "thickeners", "guar gum", "ins 412", "412", "e412", "xanthan gum", "ins 415", "415", "e415", "stabilizer", "stabilizers", "ins 410", "410", "locust bean gum", "ins 440", "440", "pectin"),
            concernLevel = "none",
            itemScore = 78,
            isFlagged = false,
            category = "Soluble Dietary Fiber",
            explanation = "Plant-derived soluble seed fibers (guar bean, fruit pectin) used to improve texture; acts as a prebiotic soluble fiber.",
            evidenceSummary = "Non-digestible plant polysaccharides that support beneficial short-chain fatty acid gut synthesis.",
            healthProblems = "Safe prebiotic fiber; excessively high amounts may cause mild temporary digestive gas.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Emulsifier (Lecithin / INS 322)",
            aliases = listOf("emulsifier", "emulsifiers", "ins 322", "322", "e322", "ins 322(i)", "soy lecithin", "sunflower lecithin", "lecithin"),
            concernLevel = "none",
            itemScore = 80,
            isFlagged = false,
            category = "Phospholipid Emulsifier",
            explanation = "Naturally occurring plant phospholipid containing essential choline; safely blends water and oil phases.",
            evidenceSummary = "Phospholipid compound naturally abundant in cell membranes; provides dietary choline.",
            healthProblems = "Nutritive phospholipid essential for cellular membranes; safe unless severe soy allergy.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Emulsifier (Mono & Diglycerides / INS 471)",
            aliases = listOf("ins 471", "471", "e471", "mono and diglycerides of fatty acids", "ins 476", "476", "polyglycerol polyricinoleate", "pgpr", "ins 442", "442", "ammonium phosphatides"),
            concernLevel = "low",
            itemScore = 65,
            isFlagged = false,
            category = "Lipid Emulsifier",
            explanation = "Food-grade fat-derived emulsifiers used to maintain smooth emulsion and texture in baked goods and chocolates.",
            evidenceSummary = "Plant fat derivatives metabolized normally as dietary fatty acids.",
            healthProblems = "May contain trace trans fat residues depending on the source oil hydrogenation level.",
            healthRisks = listOf("Trace Hydrogenated Fat Residues")
        ),
        IngredientKnowledge(
            canonicalName = "Mineral Salt (Potassium Chloride / INS 508)",
            aliases = listOf("ins 508", "508", "e508", "potassium chloride", "ins 509", "509", "calcium chloride"),
            concernLevel = "none",
            itemScore = 80,
            isFlagged = false,
            category = "Essential Mineral Salt",
            explanation = "Naturally occurring mineral salt providing dietary potassium, often used to reduce sodium content while preserving taste.",
            evidenceSummary = "Essential dietary electrolyte.",
            healthProblems = "Essential mineral electrolyte; safe for healthy individuals.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Mineral (Calcium Carbonate / INS 170)",
            aliases = listOf("mineral (calcium carbonate)", "calcium carbonate", "ins 170", "170", "e170", "mineral"),
            concernLevel = "none",
            itemScore = 82,
            isFlagged = false,
            category = "Essential Mineral",
            explanation = "Natural mineral source of elemental calcium used for dough strengthening and nutritional fortification.",
            evidenceSummary = "Standard bioavailable dietary calcium supplement.",
            healthProblems = "Bioavailable essential dietary calcium mineral; safe.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Anticaking Agent (Silica / INS 551)",
            aliases = listOf("anticaking agent", "ins 551", "551", "e551", "silicon dioxide", "silica"),
            concernLevel = "low",
            itemScore = 68,
            isFlagged = false,
            category = "Mineral Anticaking Agent",
            explanation = "Food-grade silica mineral used to prevent seasoning clumping; passes inertly through the human digestive tract.",
            evidenceSummary = "Inert mineral compound with minimal systemic gastrointestinal absorption.",
            healthProblems = "Inert mineral compound with near-zero systemic absorption; safe in regulated dietary amounts.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Leavening Agent (Ammonium Bicarbonate / INS 503)",
            aliases = listOf("leavening agent", "raising agent", "ins 503", "503", "e503", "ins 503(ii)", "ammonium bicarbonate", "baking powder"),
            concernLevel = "low",
            itemScore = 70,
            isFlagged = false,
            category = "Baking Leavening Agent",
            explanation = "Traditional baking leavening agent that releases gas during baking to create airy cracker and biscuit textures.",
            evidenceSummary = "Thermal decomposition during baking leaves no active residue in the finished food.",
            healthProblems = "Decomposes completely into carbon dioxide and ammonia gas during baking; harmless in finished baked goods.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Preservative (Potassium Sorbate / INS 202)",
            aliases = listOf("potassium sorbate", "ins 202", "202", "e202", "sorbic acid", "ins 200"),
            concernLevel = "low",
            itemScore = 65,
            isFlagged = false,
            category = "Food Preservative",
            explanation = "Widely utilized food preservative that inhibits yeast and mold proliferation, metabolizing into harmless water and carbon dioxide.",
            evidenceSummary = "Polyunsaturated fatty acid salt metabolized via standard fatty acid oxidation.",
            healthProblems = "Metabolized similarly to natural fatty acids; rare contact dermatitis in hyper-sensitive individuals.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Antioxidant (Ascorbic Acid / Vitamin C)",
            aliases = listOf("ascorbic acid", "ins 300", "300", "e300", "vitamin c", "sodium ascorbate", "ins 301", "mixed tocopherols", "vitamin e", "ins 307"),
            concernLevel = "none",
            itemScore = 90,
            isFlagged = false,
            category = "Essential Vitamin / Antioxidant",
            explanation = "Essential water-soluble dietary vitamin providing powerful physiological antioxidant protection against oxidation.",
            evidenceSummary = "Essential human nutrient supporting collagen synthesis and immune function.",
            healthProblems = "Essential human vitamin with antioxidant protection; zero toxicity.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Flavour (Natural / Nature Identical)",
            aliases = listOf("flavour", "flavours", "flavor", "flavors", "natural flavour", "natural flavor", "nature identical flavour", "nature identical flavoring", "nature identical flavouring substances", "vanillin"),
            concernLevel = "low",
            itemScore = 68,
            isFlagged = false,
            category = "Flavouring Compound",
            explanation = "Aromatic food-grade botanical or nature-identical molecules providing authentic aroma and flavor notes.",
            evidenceSummary = "FEMA GRAS evaluated flavoring molecules.",
            healthProblems = "Approved aromatic compounds; low concern.",
            healthRisks = emptyList()
        ),

        // === WHOLE FOODS, GRAINS, SPICES & HEALTHY BOTANICALS (NONE / CLEAN) ===
        IngredientKnowledge(
            canonicalName = "Whole Wheat Flour (Atta)",
            aliases = listOf("whole wheat flour", "atta", "whole wheat", "whole grain wheat", "stone ground wheat flour"),
            concernLevel = "none",
            itemScore = 90,
            isFlagged = false,
            category = "Whole Grain",
            explanation = "Nutrient-rich whole cereal grain retaining its bran, germ, and complex dietary fibers for sustained metabolic energy.",
            evidenceSummary = "High in dietary insoluble fiber, lignans, and B vitamins.",
            healthProblems = "Wholesome food providing prebiotic bran fibers and sustained energy.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Chickpea Flour (Besan)",
            aliases = listOf("chickpea flour", "besan", "gram flour", "bengal gram flour", "roasted gram"),
            concernLevel = "none",
            itemScore = 92,
            isFlagged = false,
            category = "Legume Flour",
            explanation = "High-protein, fiber-dense legume flour with a very low glycemic index, promoting satiety and steady blood glucose.",
            evidenceSummary = "Rich in plant protein, prebiotic resistant starch, and potassium.",
            healthProblems = "Clean nutrient-dense legume flour; supports healthy blood glucose.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Rolled Oats",
            aliases = listOf("oats", "rolled oats", "oat flakes", "oat flour", "whole grain oats"),
            concernLevel = "none",
            itemScore = 94,
            isFlagged = false,
            category = "Whole Grain",
            explanation = "Cardio-protective whole grain packed with beta-glucan soluble fiber known to lower LDL cholesterol.",
            evidenceSummary = "Beta-glucan soluble fiber clinically proven to reduce serum cholesterol.",
            healthProblems = "Cardioprotective beta-glucan fiber clinically proven to reduce LDL cholesterol.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Millets (Ragi / Jowar / Bajra)",
            aliases = listOf("ragi", "finger millet", "jowar", "sorghum", "bajra", "pearl millet", "millet", "millets", "foxtail millet"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Ancient Super-Grain",
            explanation = "Climate-resilient super-grains rich in calcium, iron, and slow-digesting polyphenols with a low glycemic response.",
            evidenceSummary = "Exceptional mineral density, gluten-free, and high prebiotic fiber content.",
            healthProblems = "Ancient mineral-rich superfood providing slow-release complex carbohydrates.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Milk & Dairy Solids",
            aliases = listOf("milk", "whole milk", "toned milk", "skimmed milk", "milk solids", "dairy solids", "whey", "whey protein", "casein", "cheese", "paneer", "yogurt", "curd", "milk fat"),
            concernLevel = "none",
            itemScore = 88,
            isFlagged = false,
            category = "Nutrient-Dense Dairy",
            explanation = "Whole animal dairy delivering complete essential amino acids, bioavailable calcium, and vitamin B12.",
            evidenceSummary = "Complete animal protein source rich in calcium and bioactive peptides.",
            healthProblems = "Wholesome nutrient-dense complete protein and bioavailable calcium.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Butter & Desi Ghee",
            aliases = listOf("butter", "dairy butter", "ghee", "desi ghee", "clarified butter"),
            concernLevel = "none",
            itemScore = 86,
            isFlagged = false,
            category = "Traditional Cooking Fat",
            explanation = "Traditional wholesome dairy fat rich in fat-soluble vitamins A, D, and E and anti-inflammatory gut butyrate.",
            evidenceSummary = "Contains short-chain fatty acids (butyrate) that nourish colonic epithelial cells.",
            healthProblems = "Natural saturated fat rich in butyric acid supporting gut colonocyte integrity.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Mustard Oil",
            aliases = listOf("mustard oil", "kachhi ghani mustard oil", "cold pressed mustard oil"),
            concernLevel = "none",
            itemScore = 84,
            isFlagged = false,
            category = "Traditional Culinary Oil",
            explanation = "Traditional cold-pressed Indian culinary oil featuring a balanced omega-3 to omega-6 ratio and antimicrobial allyl isothiocyanates.",
            evidenceSummary = "Rich in monounsaturated fats and alpha-linolenic acid (ALA).",
            healthProblems = "Cardioprotective monounsaturated fats and anti-microbial allyl isothiocyanates.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Groundnut / Peanut Oil",
            aliases = listOf("groundnut oil", "peanut oil", "cold pressed groundnut oil"),
            concernLevel = "none",
            itemScore = 84,
            isFlagged = false,
            category = "Culinary Seed Oil",
            explanation = "Traditional monounsaturated-rich culinary oil with high vitamin E and resveratrol content; safe unless peanut-allergic.",
            evidenceSummary = "High in heart-healthy monounsaturated oleic acid.",
            healthProblems = "Traditional culinary oil high in monounsaturated fats and vitamin E; safe.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Coconut Oil",
            aliases = listOf("coconut oil", "virgin coconut oil", "cold pressed coconut oil"),
            concernLevel = "none",
            itemScore = 86,
            isFlagged = false,
            category = "Traditional Tropical Oil",
            explanation = "Oxidatively stable traditional fat composed primarily of medium-chain triglycerides (MCTs, mainly lauric acid) easily burned for energy.",
            evidenceSummary = "Medium chain triglycerides rapidly metabolized into hepatic cellular energy.",
            healthProblems = "Medium-chain triglycerides rapidly converted to metabolic energy; heat-stable.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Cocoa (Solids & Butter)",
            aliases = listOf("cocoa solids", "cocoa butter", "cocoa powder", "cacao", "dark chocolate"),
            concernLevel = "none",
            itemScore = 90,
            isFlagged = false,
            category = "Antioxidant Botanical",
            explanation = "Natural cacao bean solids delivering powerful flavanol antioxidants (epicatechin) that support vascular nitric oxide production.",
            evidenceSummary = "Flavanols promote vascular flow-mediated dilation and neuroprotective circulation.",
            healthProblems = "Flavanols support vascular nitric oxide production and cardiovascular circulation.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Iodized Salt",
            aliases = listOf("iodized salt", "iodised salt", "salt", "sodium chloride", "rock salt", "sendha namak", "black salt", "kala namak"),
            concernLevel = "none",
            itemScore = 82,
            isFlagged = false,
            category = "Essential Mineral Seasoning",
            explanation = "Essential dietary electrolyte providing sodium and iodine necessary for proper thyroid hormone synthesis and nerve conduction.",
            evidenceSummary = "Dietary iodine prevents iodine-deficiency disorders.",
            healthProblems = "Essential electrolyte; excessive sodium intake should be moderated in hypertension.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Turmeric (Curcumin)",
            aliases = listOf("turmeric", "turmeric powder", "curcumin", "haldi", "ins 100", "100", "e100"),
            concernLevel = "none",
            itemScore = 98,
            isFlagged = false,
            category = "Antioxidant Botanical",
            explanation = "Potent botanical root spice containing curcumin, renowned in clinical medicine for systemic anti-inflammatory and cellular protection.",
            evidenceSummary = "Clinically validated NF-kB inhibitor with profound antioxidant properties.",
            healthProblems = "Potent anti-inflammatory botanical protecting cellular DNA.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Black Pepper",
            aliases = listOf("black pepper", "kali mirch", "piperine", "black pepper powder"),
            concernLevel = "none",
            itemScore = 96,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Contains bioactive piperine, which boosts intestinal nutrient absorption and enhances curcumin bioavailability by up to 2,000%.",
            evidenceSummary = "Inhibits hepatic glucuronidation, vastly elevating polyphenol bioavailability.",
            healthProblems = "Bioactive piperine enhances micronutrient absorption and digestion.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Ginger",
            aliases = listOf("ginger", "ginger powder", "sonth", "fresh ginger"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Traditional digestive rhizome containing bioactive gingerols that soothe gastrointestinal motility and reduce nausea.",
            evidenceSummary = "Anti-inflammatory gingerols modulate prostaglandin synthesis.",
            healthProblems = "Eases gastrointestinal motility and reduces oxidative inflammation.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Garlic",
            aliases = listOf("garlic", "garlic powder", "dehydrated garlic", "lehsun"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Alliin-rich botanical bulb producing allicin and prebiotic inulin fibers that benefit cardiovascular and microbiome health.",
            evidenceSummary = "Allicin supports blood pressure regulation and antimicrobial immunity.",
            healthProblems = "Allicin supports vascular health and beneficial gut microbes.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Onion",
            aliases = listOf("onion", "onion powder", "dehydrated onion", "pyaz"),
            concernLevel = "none",
            itemScore = 94,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Natural allium vegetable rich in antioxidant quercetin and organosulfur compounds that protect cellular lipids.",
            evidenceSummary = "Rich natural source of dietary quercetin and prebiotic fructooligosaccharides.",
            healthProblems = "Quercetin flavonoid antioxidant supporting cellular health.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Cumin",
            aliases = listOf("cumin", "cumin seeds", "cumin powder", "jeera"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Aromatic seed spice that stimulates pancreatic digestive enzyme secretion to enhance nutrient assimilation.",
            evidenceSummary = "Cuminaldehyde stimulates pancreatic lipase and amylase activity.",
            healthProblems = "Stimulates pancreatic digestive enzyme release.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Coriander",
            aliases = listOf("coriander", "coriander powder", "dhania", "coriander seeds"),
            concernLevel = "none",
            itemScore = 94,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Botanical culinary spice containing linalool flavonoids that support healthy blood lipid levels and digestive comfort.",
            evidenceSummary = "Antioxidant flavonoids that assist hepatic clearance of lipids.",
            healthProblems = "Carminative spice with protective flavonoids.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Chilli & Paprika",
            aliases = listOf("chili powder", "red chili", "chilli", "red chilli", "red chilli powder", "paprika", "chilli powder", "capsicum"),
            concernLevel = "none",
            itemScore = 92,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Capsaicinoid-rich spice that stimulates thermogenesis, gastric blood flow, and protective mucous secretion.",
            evidenceSummary = "Capsaicin stimulates TRPV1 receptors, boosting resting metabolic rate.",
            healthProblems = "Stimulates metabolic rate and digestive secretions; moderate in severe peptic ulcers.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Cardamom",
            aliases = listOf("cardamom", "cardamom powder", "green cardamom", "elaichi", "black cardamom"),
            concernLevel = "none",
            itemScore = 96,
            isFlagged = false,
            category = "Aromatic Spice",
            explanation = "Fragrant seed spice rich in cineole that relieves digestive bloating and freshens oral microbiota.",
            evidenceSummary = "Carminative essential oils that reduce intestinal spasms.",
            healthProblems = "Natural carminative spice relieving abdominal spasms.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Cinnamon",
            aliases = listOf("cinnamon", "cinnamon powder", "dalchini", "cassia"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Aromatic tree bark spice packed with cinnamaldehyde, clinically shown to improve peripheral insulin receptor sensitivity.",
            evidenceSummary = "Demonstrated ability to attenuate postprandial glucose surges.",
            healthProblems = "Improves peripheral cellular insulin sensitivity.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Clove",
            aliases = listOf("clove", "cloves", "clove powder", "laung"),
            concernLevel = "none",
            itemScore = 96,
            isFlagged = false,
            category = "Whole Spice",
            explanation = "Highest ORAC antioxidant rating among culinary botanicals, rich in antimicrobial and tooth-protective eugenol.",
            evidenceSummary = "Eugenol compound exerts exceptional free-radical scavenging capacity.",
            healthProblems = "High antioxidant eugenol providing cellular defense.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Asafoetida (Hing)",
            aliases = listOf("asafoetida", "hing", "heeng"),
            concernLevel = "none",
            itemScore = 92,
            isFlagged = false,
            category = "Aromatic Resin",
            explanation = "Traditional botanical gum resin that eases bloating, gas, and promotes smooth digestion of complex legumes.",
            evidenceSummary = "Carminative ferulic acid esters that stimulate digestive bile secretion.",
            healthProblems = "Relieves bloating, flatulence, and gastrointestinal spasms.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Fenugreek (Methi)",
            aliases = listOf("fenugreek", "fenugreek powder", "methi", "kasuri methi"),
            concernLevel = "none",
            itemScore = 94,
            isFlagged = false,
            category = "Botanical Herb & Seed",
            explanation = "Galactomannan soluble fiber and 4-hydroxyisoleucine in fenugreek slow intestinal carbohydrate absorption.",
            evidenceSummary = "Clinically established adjuvant for healthy glycemic control.",
            healthProblems = "Assists healthy blood glucose control and cholesterol metabolism.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Nuts & Seeds",
            aliases = listOf("almonds", "almond", "cashews", "cashew", "walnuts", "walnut", "pistachios", "pistachio", "peanuts", "peanut", "sesame seeds", "sesame", "til", "chia seeds", "flax seeds", "pumpkin seeds", "sunflower seeds"),
            concernLevel = "none",
            itemScore = 94,
            isFlagged = false,
            category = "Nutrient-Dense Nuts & Seeds",
            explanation = "Whole nutrient powerhouses rich in heart-healthy plant fats, plant protein, magnesium, and dietary fiber.",
            evidenceSummary = "Rich in monounsaturated fats, arginine, and cardioprotective phytosterols.",
            healthProblems = "Wholesome monounsaturated fats and essential micronutrients supporting heart health.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Natural Botanicals & Spices",
            aliases = listOf("spices and condiments", "spices & condiments", "mixed spices", "spices", "herbs", "condiments", "seasoning mix"),
            concernLevel = "none",
            itemScore = 88,
            isFlagged = false,
            category = "Spice Blend",
            explanation = "Wholesome blend of natural culinary botanical spices providing diverse protective polyphenols.",
            evidenceSummary = "Diverse plant polyphenols nourish commensal gut microbiome species.",
            healthProblems = "Diverse dietary polyphenols nurturing beneficial gut microbiome species.",
            healthRisks = emptyList()
        ),
        IngredientKnowledge(
            canonicalName = "Water (Aqua)",
            aliases = listOf("water", "aqua", "purified water", "filtered water", "drinking water"),
            concernLevel = "none",
            itemScore = 95,
            isFlagged = false,
            category = "Essential Hydration",
            explanation = "Pure universal solvent required for cellular homeostasis and metabolic biochemistry.",
            evidenceSummary = "Essential cellular nutrient with zero toxicity.",
            healthProblems = "Vital for hydration, kidney filtration, and enzymatic physiology.",
            healthRisks = emptyList()
        )
    )

    fun analyze(rawText: String, productName: String = ""): ProductAnalysis {
        val tokens = tokenizeAndNormalize(rawText)
        if (tokens.isEmpty()) {
            return ProductAnalysis(
                score = 100,
                statusLabel = "CLEAN",
                keyConcerns = emptyList(),
                summaryNarrative = "No ingredients were provided for evaluation.",
                totalIngredients = 0,
                flaggedIngredients = emptyList(),
                allIngredients = emptyList(),
                matchedIngredients = emptyList(),
                unknownIngredients = emptyList()
            )
        }

        val allItems = mutableListOf<EvaluatedIngredient>()
        val flaggedItems = mutableListOf<EvaluatedIngredient>()
        val unknownNames = mutableListOf<String>()

        for (token in tokens) {
            val matched = findInCatalog(token)
            if (matched != null) {
                val evaluated = EvaluatedIngredient(
                    name = capitalizeWords(token),
                    canonicalName = matched.canonicalName,
                    concernLevel = matched.concernLevel,
                    score = matched.itemScore,
                    isFlagged = matched.isFlagged,
                    explanation = matched.explanation,
                    category = matched.category,
                    description = matched.explanation,
                    evidenceSummary = matched.evidenceSummary,
                    sources = matched.sources,
                    healthProblems = matched.healthProblems,
                    healthRisks = matched.healthRisks
                )
                allItems.add(evaluated)
                if (matched.isFlagged) {
                    flaggedItems.add(evaluated)
                }
            } else {
                // Classify via intelligent heuristic / INS code resolver
                val classified = classifyIngredientHeuristically(token)
                allItems.add(classified)
                if (classified.isFlagged) {
                    flaggedItems.add(classified)
                }
                if (classified.concernLevel == "unknown") {
                    unknownNames.add(capitalizeWords(token))
                }
            }
        }

        // Deduplicate ingredients by canonical name for scoring penalties
        val uniqueItems = allItems.distinctBy { it.canonicalName.lowercase() }
        val uniqueFlagged = flaggedItems.distinctBy { it.canonicalName.lowercase() }

        // === DETERMINISTIC PURESCAN SCORING (docs/scoring_spec.md & AGENTS.md) ===
        // "Start at 100 and apply configurable penalties based on the application's own ingredient database.
        // NONE: 0, LOW: 2, MODERATE: 6, HIGH: 12. Unknown is not harmful (0 penalty). Clamp 0-100."
        var totalPenalties = 0
        for (item in uniqueItems) {
            val penalty = when (item.concernLevel.lowercase()) {
                "high" -> PENALTY_HIGH
                "moderate" -> PENALTY_MODERATE
                "low" -> PENALTY_LOW
                else -> PENALTY_NONE // "none" and "unknown" carry 0 penalty
            }
            totalPenalties += penalty
        }

        // Final score: 100 minus sum of penalties, clamped between 0 and 100
        val finalScore = (100 - totalPenalties).coerceIn(0, 100)

        // Status Label: CLEAN (>=80), CAUTION (50-79), AVOID (<50)
        val statusLabel = when {
            finalScore >= 80 -> "CLEAN"
            finalScore >= 50 -> "CAUTION"
            else -> "AVOID"
        }

        // Extract key concerns specific to what is actually in this product
        val keyConcerns = mutableListOf<String>()
        if (uniqueItems.any { it.canonicalName.contains("Palm", ignoreCase = true) || it.name.contains("Palm", ignoreCase = true) }) {
            keyConcerns.add("PALM OIL")
        }
        if (uniqueItems.any { it.canonicalName.contains("Trans", ignoreCase = true) || it.canonicalName.contains("Hydrogenated", ignoreCase = true) }) {
            keyConcerns.add("TRANS FATS")
        }
        if (uniqueItems.any { it.canonicalName.contains("Flavour Enhancer", ignoreCase = true) || it.name.contains("627", ignoreCase = true) || it.name.contains("631", ignoreCase = true) || it.name.contains("635", ignoreCase = true) }) {
            keyConcerns.add("FLAVOUR ENHANCERS")
        }
        if (uniqueItems.any { it.canonicalName.contains("Colour", ignoreCase = true) || it.category.contains("Dye", ignoreCase = true) || it.name.contains("150d", ignoreCase = true) || it.name.contains("102", ignoreCase = true) || it.name.contains("110", ignoreCase = true) }) {
            keyConcerns.add("ARTIFICIAL COLOUR")
        }
        if (uniqueItems.any { it.canonicalName.contains("TBHQ", ignoreCase = true) || it.canonicalName.contains("BHA", ignoreCase = true) || it.canonicalName.contains("BHT", ignoreCase = true) || it.name.contains("319", ignoreCase = true) }) {
            keyConcerns.add("SYNTHETIC PRESERVATIVE")
        }
        if (uniqueItems.any { it.canonicalName.contains("Maida", ignoreCase = true) || it.canonicalName.contains("Refined Wheat", ignoreCase = true) }) {
            keyConcerns.add("REFINED FLOUR")
        }
        if (uniqueItems.any { it.canonicalName.contains("Sugar", ignoreCase = true) || it.canonicalName.contains("Maltodextrin", ignoreCase = true) || it.canonicalName.contains("HFCS", ignoreCase = true) }) {
            keyConcerns.add("ADDED SUGARS / HIGH GI")
        }
        if (uniqueItems.any { it.category.contains("Seed Oil", ignoreCase = true) || it.name.contains("Seed Oil", ignoreCase = true) }) {
            keyConcerns.add("REFINED SEED OILS")
        }
        if (uniqueItems.any { it.canonicalName.contains("Aspartame", ignoreCase = true) || it.canonicalName.contains("Sucralose", ignoreCase = true) || it.canonicalName.contains("Acesulfame", ignoreCase = true) }) {
            keyConcerns.add("ARTIFICIAL SWEETENERS")
        }

        if (keyConcerns.isEmpty() && uniqueFlagged.isNotEmpty()) {
            keyConcerns.add(uniqueFlagged.first().canonicalName.uppercase())
        }
        if (keyConcerns.isEmpty()) {
            keyConcerns.add("CLEAN FORMULATION")
            keyConcerns.add("WHOLE INGREDIENTS")
        }

        val narrative = when {
            finalScore >= 80 -> {
                "Clean formulation composed predominantly of whole ingredients with zero high-concern additives, synthetic colours, or industrial palm oil."
            }
            finalScore >= 50 -> {
                "Moderate concern formulation containing ${uniqueFlagged.take(3).joinToString(", ") { it.canonicalName.lowercase() }}. Contains refined components or food additives. Consume occasionally as part of a balanced diet."
            }
            else -> {
                "Highly processed formulation flagged for ${uniqueFlagged.take(3).joinToString(", ") { it.canonicalName.lowercase() }}. Built on industrial refined oils and additives linked to elevated cardiovascular, metabolic, and inflammatory health risks. Look for cleaner whole-food alternatives."
            }
        }

        return ProductAnalysis(
            score = finalScore,
            statusLabel = statusLabel,
            keyConcerns = keyConcerns.take(3),
            summaryNarrative = narrative,
            totalIngredients = allItems.size,
            flaggedIngredients = uniqueFlagged,
            allIngredients = allItems,
            matchedIngredients = allItems.filter { it.concernLevel != "unknown" },
            unknownIngredients = unknownNames
        )
    }

    private fun findInCatalog(token: String): IngredientKnowledge? {
        val lower = token.lowercase().trim()
        val normalizedToken = lower.replace(Regex("[^a-z0-9]"), " ").replace(Regex("\\s+"), " ").trim()

        return catalog.firstOrNull { entry ->
            val entryNorm = entry.canonicalName.lowercase().replace(Regex("[^a-z0-9]"), " ").replace(Regex("\\s+"), " ").trim()
            if (normalizedToken == entryNorm) return@firstOrNull true

            entry.aliases.any { alias ->
                val aliasNorm = alias.lowercase().replace(Regex("[^a-z0-9]"), " ").replace(Regex("\\s+"), " ").trim()
                normalizedToken == aliasNorm ||
                (aliasNorm.length >= 4 && normalizedToken.contains(aliasNorm)) ||
                (normalizedToken.length >= 5 && aliasNorm.contains(normalizedToken))
            }
        }
    }

    /**
     * Intelligent heuristic classifier for ingredients not found in the static catalog.
     * Evaluates INS/E-codes, botanical markers, chemical suffixes, and common food components.
     * Truly unidentifiable tokens receive 'unknown' with 0 penalty, strictly compliant with AGENTS.md.
     */
    private fun classifyIngredientHeuristically(token: String): EvaluatedIngredient {
        val lower = token.lowercase().trim()
        val capitalized = capitalizeWords(token)

        // 1. Detect INS / E Numbers
        val insMatch = Regex("(?:ins|e)?\\s*(\\d{3,4}[a-z]?(?:\\([i-v]+\\))?)").find(lower)
        if (insMatch != null) {
            val codeStr = insMatch.groupValues[1]
            val baseNumber = Regex("^(\\d{3,4})").find(codeStr)?.groupValues?.get(1)?.toIntOrNull()

            if (baseNumber != null) {
                when (baseNumber) {
                    // 100-199: Colours
                    in 100..109, in 111..121, in 123..126, in 128..139, in 151..159, in 171..199 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Food Colour ($codeStr)",
                            concernLevel = "high",
                            score = 20,
                            isFlagged = true,
                            category = "Synthetic Colouring",
                            explanation = "Synthetic or regulated colouring agent (INS $codeStr). Artificial dyes are associated with hypersensitivity and hyperactivity.",
                            evidenceSummary = "Regulated synthetic food dye.",
                            healthProblems = "Childhood hyperactivity, allergic sensitivity reactions, chronic skin hives (urticaria), and cellular stress.",
                            healthRisks = listOf("Hyperactivity", "Allergic Urticaria", "Cellular Stress")
                        )
                    }
                    110, 102, 122, 124, 127, 129, 133 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Synthetic Azo Dye ($codeStr)",
                            concernLevel = "high",
                            score = 18,
                            isFlagged = true,
                            category = "Synthetic Azo Dye",
                            explanation = "Azo dye (INS $codeStr) restricted in the EU and linked with adverse reactions in sensitive individuals.",
                            evidenceSummary = "Azo dye requiring health warnings in several jurisdictions.",
                            healthProblems = "ADHD-like behavioral hyperactivity in children, asthma aggravation, allergic hives, and potential genotoxicity.",
                            healthRisks = listOf("ADHD-like Hyperactivity", "Asthma Aggravation", "EU Warning Restriction")
                        )
                    }
                    140, 141, 160, 162, 163, 170 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Natural Colour / Mineral ($codeStr)",
                            concernLevel = "none",
                            score = 85,
                            isFlagged = false,
                            category = "Natural Botanical / Mineral",
                            explanation = "Plant-derived natural pigment or mineral (INS $codeStr) with a clean safety profile.",
                            evidenceSummary = "Natural food pigment.",
                            healthProblems = "Plant pigment or mineral; safe for regular consumption.",
                            healthRisks = emptyList()
                        )
                    }
                    150 -> {
                        val isHigh = codeStr.contains("d", true) || codeStr.contains("c", true)
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Caramel Colour ($codeStr)",
                            concernLevel = if (isHigh) "high" else "moderate",
                            score = if (isHigh) 22 else 38,
                            isFlagged = true,
                            category = "Food Colour",
                            explanation = "Processed caramel colour ($codeStr) produced using thermal and chemical treatment.",
                            evidenceSummary = "Potential presence of 4-MEI processing byproducts.",
                            healthProblems = "Presence of 4-methylimidazole (4-MEI), a WHO IARC Group 2B possible human carcinogen; cellular oxidative stress.",
                            healthRisks = listOf("Carcinogen 4-MEI Exposure", "Cellular Stress")
                        )
                    }

                    // 200-299: Preservatives
                    210, 211, 212, 213 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Benzoate Preservative ($codeStr)",
                            concernLevel = "moderate",
                            score = 36,
                            isFlagged = true,
                            category = "Preservative",
                            explanation = "Chemical benzoate preservative (INS $codeStr). Can react with vitamin C to form trace benzene.",
                            evidenceSummary = "Antimicrobial food preservative.",
                            healthProblems = "Can react with vitamin C to synthesize trace carcinogenic benzene; triggers bronchospasm in asthmatic consumers.",
                            healthRisks = listOf("Benzene Formation Risk", "Asthmatic Bronchospasm")
                        )
                    }
                    220, 221, 222, 223, 224, 225, 226, 227, 228 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Sulphite Preservative ($codeStr)",
                            concernLevel = "moderate",
                            score = 35,
                            isFlagged = true,
                            category = "Sulphite Preservative",
                            explanation = "Sulphite preservative (INS $codeStr) that can trigger asthmatic and allergic reactions.",
                            evidenceSummary = "Known allergen declaring mandatory caution.",
                            healthProblems = "Acute asthmatic bronchospasm, skin flushing, gastrointestinal irritation, and thiamine (vitamin B1) destruction.",
                            healthRisks = listOf("Severe Asthmatic Attacks", "Thiamine Depletion")
                        )
                    }
                    249, 250, 251, 252 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Nitrite / Nitrate Preservative ($codeStr)",
                            concernLevel = "high",
                            score = 15,
                            isFlagged = true,
                            category = "Nitrite Preservative",
                            explanation = "Nitrite preservative (INS $codeStr) linked to carcinogenic nitrosamine formation during high-heat cooking.",
                            evidenceSummary = "Forms carcinogenic nitrosamines in meat processing.",
                            healthProblems = "Reacts with amines under digestive and cooking heat to generate nitrosamines, classified as carcinogenic.",
                            healthRisks = listOf("Carcinogenic Nitrosamines", "Colorectal Cancer Risk")
                        )
                    }
                    in 200..299 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Food Preservative ($codeStr)",
                            concernLevel = "low",
                            score = 65,
                            isFlagged = false,
                            category = "Preservative",
                            explanation = "Regulated food preservative (INS $codeStr) that prevents microbial spoilage.",
                            evidenceSummary = "Standard approved preservative.",
                            healthProblems = "Low health concern in regulated dietary amounts.",
                            healthRisks = emptyList()
                        )
                    }

                    // 300-321: Antioxidants
                    319, 320, 321 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Synthetic Antioxidant ($codeStr)",
                            concernLevel = "high",
                            score = 18,
                            isFlagged = true,
                            category = "Synthetic Antioxidant",
                            explanation = "Petroleum-derived synthetic antioxidant (INS $codeStr, such as TBHQ, BHA, or BHT).",
                            evidenceSummary = "Cellular and immune alterations in animal toxicology tests.",
                            healthProblems = "Immune dysregulation, hepatic enzyme disruption, cellular oxidative DNA stress, and endocrine interference.",
                            healthRisks = listOf("Immune Modulation", "Hepatic Stress", "Endocrine Interference")
                        )
                    }
                    300, 301, 302, 306, 307, 308, 309 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Vitamin Antioxidant ($codeStr)",
                            concernLevel = "none",
                            score = 90,
                            isFlagged = false,
                            category = "Vitamin / Nutrient",
                            explanation = "Vitamin-derived antioxidant (INS $codeStr, Vitamin C or Vitamin E tocopherol) protecting freshness.",
                            evidenceSummary = "Essential dietary antioxidant vitamin.",
                            healthProblems = "Wholesome dietary antioxidant; supports immunity.",
                            healthRisks = emptyList()
                        )
                    }

                    // 322-399: Acidity Regulators & Emulsifiers
                    322 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Lecithin ($codeStr)",
                            concernLevel = "none",
                            score = 80,
                            isFlagged = false,
                            category = "Phospholipid Emulsifier",
                            explanation = "Natural plant-derived phospholipid emulsifier rich in essential choline.",
                            evidenceSummary = "Safe natural phospholipid.",
                            healthProblems = "Essential phospholipid nutrient; safe.",
                            healthRisks = emptyList()
                        )
                    }
                    330, 331, 332, 334 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Acidity Regulator ($codeStr)",
                            concernLevel = "none",
                            score = 75,
                            isFlagged = false,
                            category = "Food Acid / Buffer",
                            explanation = "Natural food acid or buffer (INS $codeStr, Citric/Tartaric/Malic acid) regulating pH balance.",
                            evidenceSummary = "Naturally occurring physiological organic acid.",
                            healthProblems = "Naturally occurring organic acid intermediate; safe.",
                            healthRisks = emptyList()
                        )
                    }
                    338, 339, 340, 341 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Phosphate Regulator ($codeStr)",
                            concernLevel = "moderate",
                            score = 45,
                            isFlagged = true,
                            category = "Phosphate Additive",
                            explanation = "Inorganic phosphate compound (INS $codeStr); excess dietary phosphates contribute to vascular calcification.",
                            evidenceSummary = "Inorganic dietary phosphate.",
                            healthProblems = "Arterial wall calcification, accelerated renal filtration strain, and disruption of bone calcium homeostasis.",
                            healthRisks = listOf("Vascular Calcification", "Kidney Strain")
                        )
                    }

                    // 400-499: Thickeners, Gums, Emulsifiers
                    407 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Carrageenan ($codeStr)",
                            concernLevel = "moderate",
                            score = 42,
                            isFlagged = true,
                            category = "Vegetable Gum",
                            explanation = "Seaweed-derived carrageenan stabilizer (INS $codeStr) linked to intestinal inflammation in animal models.",
                            evidenceSummary = "Potential gut mucosal barrier disruption.",
                            healthProblems = "Intestinal mucosal barrier erosion, chronic gut inflammation, and exacerbation of ulcerative colitis/IBD.",
                            healthRisks = listOf("Intestinal Ulceration", "Colitis Flares", "Leaky Gut")
                        )
                    }
                    410, 412, 414, 415, 440 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Natural Plant Gum ($codeStr)",
                            concernLevel = "none",
                            score = 78,
                            isFlagged = false,
                            category = "Vegetable Gum / Fiber",
                            explanation = "Plant-derived soluble fiber gum (INS $codeStr, Guar/Xanthan/Acacia) providing texture and prebiotic fiber.",
                            evidenceSummary = "Soluble dietary fiber with positive prebiotic properties.",
                            healthProblems = "Safe prebiotic dietary fiber; supports healthy colonic fermentation.",
                            healthRisks = emptyList()
                        )
                    }
                    450, 451, 452 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Polyphosphate Humectant ($codeStr)",
                            concernLevel = "moderate",
                            score = 45,
                            isFlagged = true,
                            category = "Phosphate Additive",
                            explanation = "Inorganic polyphosphate salt (INS $codeStr) used for water retention and dough texture.",
                            evidenceSummary = "Inorganic phosphate additive.",
                            healthProblems = "Vascular stiffness, accelerated coronary calcification, and long-term renal filtration burden.",
                            healthRisks = listOf("Arterial Calcification", "Renal Overload")
                        )
                    }
                    471, 472, 476 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Lipid Emulsifier ($codeStr)",
                            concernLevel = "low",
                            score = 65,
                            isFlagged = false,
                            category = "Emulsifier",
                            explanation = "Fatty acid ester emulsifier (INS $codeStr) used to blend ingredients and stabilize textures.",
                            evidenceSummary = "Standard plant lipid emulsifier.",
                            healthProblems = "Low health concern; potential trace trans fats depending on processing.",
                            healthRisks = emptyList()
                        )
                    }

                    // 500-599: Mineral Salts & Leavening
                    500, 501, 503, 508, 509 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Mineral Salt / Leavening ($codeStr)",
                            concernLevel = "none",
                            score = 80,
                            isFlagged = false,
                            category = "Mineral Salt",
                            explanation = "Food-grade mineral salt or leavening agent (INS $codeStr, Sodium/Potassium bicarbonate) controlling dough texture.",
                            evidenceSummary = "Non-toxic mineral salt.",
                            healthProblems = "Essential mineral electrolytes; non-toxic.",
                            healthRisks = emptyList()
                        )
                    }
                    551, 552, 553 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Anticaking Agent ($codeStr)",
                            concernLevel = "low",
                            score = 68,
                            isFlagged = false,
                            category = "Anticaking Agent",
                            explanation = "Inert mineral compound (INS $codeStr) used to maintain free-flowing powder seasonings.",
                            evidenceSummary = "Passes inertly through gastrointestinal tract.",
                            healthProblems = "Inert mineral; passes unabsorbed through the digestive tract.",
                            healthRisks = emptyList()
                        )
                    }

                    // 600-699: Flavour Enhancers
                    in 600..699 -> {
                        val isHigh = baseNumber in listOf(627, 631, 635)
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Flavour Enhancer ($codeStr)",
                            concernLevel = if (isHigh) "high" else "moderate",
                            score = 25,
                            isFlagged = true,
                            category = "Flavor Enhancer",
                            explanation = "Purine or glutamate taste enhancer (INS $codeStr) that synthetically stimulates savoriness.",
                            evidenceSummary = "Ribonucleotide or glutamate taste stimulator.",
                            healthProblems = "Stimulates intense hedonic cravings and overeating; purine metabolism triggers elevated uric acid and gout attacks.",
                            healthRisks = listOf("Hedonic Cravings", "Gout Flares (Uric Acid)")
                        )
                    }

                    // 900-999: Sweeteners & Glazing
                    950, 951, 954, 955 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Artificial Sweetener ($codeStr)",
                            concernLevel = "high",
                            score = 22,
                            isFlagged = true,
                            category = "Artificial Sweetener",
                            explanation = "High-intensity artificial sweetener (INS $codeStr) associated with gut microbiome dysbiosis.",
                            evidenceSummary = "Non-caloric chemical artificial sweetener.",
                            healthProblems = "Depletes beneficial gut microbiota, impairs metabolic insulin sensitivity, and possible carcinogen classification (Aspartame IARC 2B).",
                            healthRisks = listOf("Gut Microbiome Dysbiosis", "Impaired Insulin Sensitivity", "Possible Carcinogen (IARC 2B)")
                        )
                    }
                    960 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Steviol Glycosides ($codeStr)",
                            concernLevel = "low",
                            score = 75,
                            isFlagged = false,
                            category = "Botanical Sweetener",
                            explanation = "Plant-derived non-caloric sweetener extracted from the Stevia rebaudiana plant.",
                            evidenceSummary = "Natural plant-extracted zero-calorie sweetener.",
                            healthProblems = "Natural plant extract; safe zero-calorie alternative to sugar.",
                            healthRisks = emptyList()
                        )
                    }

                    // 1400-1499: Modified Starches
                    in 1400..1499 -> {
                        return EvaluatedIngredient(
                            name = capitalized,
                            canonicalName = "Modified Starch ($codeStr)",
                            concernLevel = "low",
                            score = 65,
                            isFlagged = false,
                            category = "Modified Starch",
                            explanation = "Chemically or physically modified food starch (INS $codeStr) used for thickening and freeze-thaw stability.",
                            evidenceSummary = "Refined modified carbohydrate.",
                            healthProblems = "Refined carbohydrate; contributes to glycemic load.",
                            healthRisks = listOf("Glycemic Load")
                        )
                    }
                }
            }
        }

        // 2. Semantic text & chemical family heuristics
        return when {
            lower.contains("palm") || lower.contains("palmolein") || lower.contains("vanaspati") || lower.contains("hydrogenated") || lower.contains("shortening") || lower.contains("trans fat") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "high",
                    score = 20,
                    isFlagged = true,
                    category = "Industrial Lipid / Oil",
                    explanation = "Processed industrial lipid high in saturated palmitic acid or synthetic trans fats.",
                    evidenceSummary = "Atherogenic lipid profile.",
                    healthProblems = "Elevated LDL cholesterol, arterial plaque build-up, coronary heart disease, and liver lipid accumulation.",
                    healthRisks = listOf("Cardiovascular Disease", "Arterial Plaque", "LDL Cholesterol Spikes")
                )
            }
            lower.contains("oil") || lower.contains("fat") || lower.contains("butter") || lower.contains("ghee") || lower.contains("lipid") -> {
                val isClean = lower.contains("olive") || lower.contains("coconut") || lower.contains("butter") || lower.contains("ghee") || lower.contains("mustard")
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = if (isClean) "none" else "moderate",
                    score = if (isClean) 84 else 45,
                    isFlagged = !isClean,
                    category = "Fats & Oils",
                    explanation = if (isClean) "Traditional wholesome culinary lipid." else "Refined industrial seed oil high in omega-6.",
                    evidenceSummary = "Dietary lipid.",
                    healthProblems = if (isClean) "Safe traditional dietary fat." else "High omega-6 linoleic acid ratio, cellular lipid oxidation, and pro-inflammatory signaling.",
                    healthRisks = if (isClean) emptyList() else listOf("Inflammatory Omega-6 Ratio", "Lipid Oxidation")
                )
            }
            lower.contains("sugar") || lower.contains("syrup") || lower.contains("fructose") || lower.contains("sucrose") || lower.contains("glucose") || lower.contains("dextrose") || lower.endsWith("ose") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "moderate",
                    score = 42,
                    isFlagged = true,
                    category = "Refined Sweetener",
                    explanation = "Refined caloric sweetener that increases postprandial glucose and hepatic lipogenesis.",
                    evidenceSummary = "Caloric sugar with fast glycemic absorption.",
                    healthProblems = "Rapid insulin spikes, non-alcoholic fatty liver disease (NAFLD), elevated triglycerides, and dental caries.",
                    healthRisks = listOf("Insulin Surges", "Fatty Liver Risk", "Dental Caries")
                )
            }
            lower.contains("aspartame") || lower.contains("sucralose") || lower.contains("saccharin") || lower.contains("acesulfame") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "high",
                    score = 22,
                    isFlagged = true,
                    category = "Artificial Sweetener",
                    explanation = "Non-caloric chemical artificial sweetener.",
                    evidenceSummary = "Chemical sweetener linked to metabolic alterations.",
                    healthProblems = "Gut microbiome dysbiosis, impaired insulin sensitivity, possible carcinogenicity (IARC 2B), and persistent sweet palate conditioning.",
                    healthRisks = listOf("Gut Microbiome Dysbiosis", "Impaired Insulin Sensitivity", "IARC 2B Potential Carcinogen")
                )
            }
            lower.contains("flour") || lower.contains("starch") || lower.contains("dextrin") || lower.contains("maida") || lower.contains("semolina") || lower.contains("suji") || lower.contains("atta") -> {
                val isWhole = lower.contains("whole") || lower.contains("atta") || lower.contains("besan") || lower.contains("gram") || lower.contains("oat") || lower.contains("ragi")
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = if (isWhole) "none" else "moderate",
                    score = if (isWhole) 88 else 45,
                    isFlagged = !isWhole,
                    category = if (isWhole) "Whole Grain / Legume" else "Refined Grain",
                    explanation = if (isWhole) "Nutrient-dense grain flour retaining dietary fibers and micronutrients." else "Refined cereal carbohydrate.",
                    evidenceSummary = if (isWhole) "High fiber whole grain." else "Refined starch.",
                    healthProblems = if (isWhole) "Safe whole-grain component supporting digestive satiety." else "Rapid glucose spike followed by insulin crash, risk of insulin resistance, and visceral fat storage.",
                    healthRisks = if (isWhole) emptyList() else listOf("Blood Sugar Spikes", "Insulin Resistance", "Visceral Fat Storage")
                )
            }
            lower.contains("powder") || lower.contains("spice") || lower.contains("herb") || lower.contains("leaf") || lower.contains("seed") || lower.contains("extract") || lower.contains("root") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "none",
                    score = 88,
                    isFlagged = false,
                    category = "Botanical / Spice",
                    explanation = "Natural plant-derived botanical or culinary spice supplying natural polyphenols.",
                    evidenceSummary = "Natural culinary botanical.",
                    healthProblems = "Safe natural botanical containing protective polyphenols.",
                    healthRisks = emptyList()
                )
            }
            lower.contains("milk") || lower.contains("whey") || lower.contains("dairy") || lower.contains("cheese") || lower.contains("casein") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "none",
                    score = 85,
                    isFlagged = false,
                    category = "Dairy Nutrient",
                    explanation = "Wholesome dairy component rich in essential amino acids and calcium.",
                    evidenceSummary = "Complete protein dairy source.",
                    healthProblems = "Nutritive whole protein and bioavailable calcium.",
                    healthRisks = emptyList()
                )
            }
            lower.contains("acid") || lower.contains("salt") || lower.contains("carbonate") || lower.contains("chloride") || lower.contains("citrate") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "none",
                    score = 75,
                    isFlagged = false,
                    category = "Mineral & pH Regulator",
                    explanation = "Food-grade mineral salt or organic acid controlling flavor and stability.",
                    evidenceSummary = "Standard food buffering agent.",
                    healthProblems = "Safe food-grade mineral or organic acid.",
                    healthRisks = emptyList()
                )
            }
            lower.contains("gum") || lower.contains("pectin") || lower.contains("agar") || lower.contains("alginate") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "none",
                    score = 78,
                    isFlagged = false,
                    category = "Vegetable Gum / Fiber",
                    explanation = "Natural plant polysaccharide used for texture stabilization.",
                    evidenceSummary = "Dietary soluble fiber.",
                    healthProblems = "Safe prebiotic soluble dietary fiber.",
                    healthRisks = emptyList()
                )
            }
            lower.contains("flavour") || lower.contains("flavor") || lower.contains("aroma") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "low",
                    score = 68,
                    isFlagged = false,
                    category = "Flavouring",
                    explanation = "Food-grade aromatic flavoring compound.",
                    evidenceSummary = "Approved food flavoring.",
                    healthProblems = "Low health concern in culinary amounts.",
                    healthRisks = emptyList()
                )
            }
            lower.contains("preservative") || lower.contains("antioxidant") || lower.contains("tbhq") || lower.contains("benzoate") -> {
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "moderate",
                    score = 35,
                    isFlagged = true,
                    category = "Preservative Additive",
                    explanation = "Food preservation additive preventing degradation.",
                    evidenceSummary = "Preservative agent.",
                    healthProblems = "Hypersensitivity allergic reactions, cellular oxidative stress, and gut microbial disruption.",
                    healthRisks = listOf("Hypersensitivity", "Oxidative Stress")
                )
            }
            else -> {
                // Truly uncataloged ingredient: 0 penalty per AGENTS.md ("Unknown is not harmful")
                EvaluatedIngredient(
                    name = capitalized,
                    canonicalName = capitalized,
                    concernLevel = "unknown",
                    score = 70,
                    isFlagged = false,
                    explanation = "Uncataloged ingredient evaluated with standard zero-penalty baseline parameters.",
                    category = "Uncataloged Ingredient",
                    description = "Uncataloged ingredient",
                    evidenceSummary = "Unknown ingredients are not automatically harmful (0 penalty).",
                    healthProblems = "",
                    healthRisks = emptyList()
                )
            }
        }
    }

    /**
     * Balanced bracket-aware tokenizer that unpacks compound ingredients,
     * preserves sub-ingredients, and extracts individual additives cleanly.
     */
    private fun tokenizeAndNormalize(raw: String): List<String> {
        // Strip section headers
        val cleaned = raw
            .replace(Regex("(?i)\\b(and\\s+)?(noodles|seasoning|tastemaker|contains|ingredients|note|storage|allergen\\s*information)\\s*:"), ",")
            .replace(Regex("\\b\\d+(\\.\\d+)?%\\b"), "")
            .replace("*", "")

        val extractedTokens = mutableListOf<String>()
        var i = 0
        val sb = StringBuilder()

        while (i < cleaned.length) {
            val char = cleaned[i]
            if (char == '(' || char == '[') {
                val closeChar = if (char == '(') ')' else ']'
                val parentCategory = sb.toString().trim()
                sb.clear()

                // Find matching closing parenthesis
                var depth = 1
                val inner = StringBuilder()
                i++
                while (i < cleaned.length && depth > 0) {
                    if (cleaned[i] == char) depth++
                    else if (cleaned[i] == closeChar) depth--

                    if (depth > 0) {
                        inner.append(cleaned[i])
                    }
                    i++
                }

                val innerContent = inner.toString().trim()
                if (innerContent.isNotEmpty()) {
                    // Check if inner content contains comma-separated or ampersand items
                    val subItems = innerContent.split(Regex("[,;&]"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    if (subItems.size > 1) {
                        // Unpack each sub-ingredient
                        for (sub in subItems) {
                            val cleanSub = sub.removePrefix("and ").removePrefix("with ").trim()
                            if (cleanSub.isNotEmpty()) {
                                if (parentCategory.isNotEmpty() && !parentCategory.equals("spices and condiments", true)) {
                                    extractedTokens.add("$parentCategory ($cleanSub)")
                                } else {
                                    extractedTokens.add(cleanSub)
                                }
                            }
                        }
                    } else {
                        // Single item in brackets: keep compound or extract
                        if (parentCategory.isNotEmpty()) {
                            extractedTokens.add("$parentCategory ($innerContent)")
                        } else {
                            extractedTokens.add(innerContent)
                        }
                    }
                } else if (parentCategory.isNotEmpty()) {
                    extractedTokens.add(parentCategory)
                }
            } else if (char == ',' || char == ';' || char == '•' || char == '\n') {
                val token = sb.toString().trim()
                if (token.isNotEmpty()) {
                    extractedTokens.add(token)
                }
                sb.clear()
                i++
            } else {
                sb.append(char)
                i++
            }
        }

        val lastToken = sb.toString().trim()
        if (lastToken.isNotEmpty()) {
            extractedTokens.add(lastToken)
        }

        return extractedTokens
            .map { it.trim().removeSuffix(".").removeSuffix(":") }
            .filter { token ->
                val clean = token.trim()
                clean.length >= 2 &&
                !clean.equals("and", ignoreCase = true) &&
                !clean.equals("with", ignoreCase = true) &&
                !clean.equals("or", ignoreCase = true) &&
                !clean.endsWith(":")
            }
    }

    private fun capitalizeWords(str: String): String {
        return str.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
