import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/network/dio_client.dart';
import '../models/product_dto.dart';

abstract class ProductApiService {
  Future<ProductDto?> fetchProductByBarcode(String barcode);
  Future<List<ProductDto>> searchProducts(String query);
}

class ProductApiServiceImpl implements ProductApiService {
  final Dio _dio;

  ProductApiServiceImpl(this._dio);

  @override
  Future<ProductDto?> fetchProductByBarcode(String barcode) async {
    final clean = barcode.trim();
    if (clean.isEmpty) return null;

    // 1. Try Open Food Facts India (https://in.openfoodfacts.org) v2
    final indiaV2Dto = await _fetchFromUrl('${ApiConstants.openFoodFactsIndiaBase}/$clean.json', clean);
    if (indiaV2Dto != null) return indiaV2Dto;

    // 2. Try Open Food Facts India v0
    final indiaV0Dto = await _fetchFromUrl('${ApiConstants.openFoodFactsIndiaV0Base}/$clean.json', clean);
    if (indiaV0Dto != null) return indiaV0Dto;

    // 3. Fallback to Global Open Food Facts
    final globalFoodDto = await _fetchFromUrl('${ApiConstants.openFoodFactsGlobalBase}/$clean.json', clean);
    if (globalFoodDto != null) return globalFoodDto;

    // 4. Fallback to Open Beauty Facts
    final beautyDto = await _fetchFromUrl('${ApiConstants.openBeautyFactsBase}/$clean.json', clean);
    return beautyDto;
  }

  Future<ProductDto?> _fetchFromUrl(String url, String barcode) async {
    try {
      final response = await _dio.get<Map<String, dynamic>>(
        url,
        options: Options(
          headers: {
            'User-Agent': 'PureScan-India - OpenFoodFacts India Client'
          },
        ),
      );

      if (response.statusCode == 200 && response.data != null) {
        final data = response.data!;
        final status = data['status'];
        if (status == 1 && data.containsKey('product')) {
          final productJson = data['product'] as Map<String, dynamic>;
          final name = (productJson['product_name'] ?? productJson['product_name_en'] ?? productJson['generic_name'] ?? 'Product #$barcode').toString();
          final brand = (productJson['brands'] ?? productJson['brand_owner'] ?? 'Indian Brand').toString();
          final category = (productJson['categories'] ?? 'Indian Packaged Food').toString().split(',').first.trim();
          final imageUrl = productJson['image_url'] ?? productJson['image_front_url'];
          final ingredients = (productJson['ingredients_text'] ?? productJson['ingredients_text_en'] ?? '').toString();

          return ProductDto(
            barcode: barcode,
            name: name,
            brand: brand,
            imageUrl: imageUrl?.toString(),
            category: category,
            ingredientsText: ingredients,
          );
        }
      }
      return null;
    } on DioException {
      return null;
    } catch (_) {
      return null;
    }
  }

  @override
  Future<List<ProductDto>> searchProducts(String query) async {
    final clean = query.trim();
    if (clean.isEmpty) return [];

    try {
      final response = await _dio.get<Map<String, dynamic>>(
        'https://in.openfoodfacts.org/cgi/search.pl',
        queryParameters: {
          'search_terms': clean,
          'search_simple': 1,
          'action': 'process',
          'json': 1,
          'page_size': 20,
        },
      );

      if (response.statusCode == 200 && response.data != null) {
        final productsList = response.data!['products'];
        if (productsList is List) {
          return productsList.map((item) {
            final p = item as Map<String, dynamic>;
            final code = (p['code'] ?? '').toString();
            final name = (p['product_name'] ?? p['product_name_en'] ?? 'Unknown Indian Product').toString();
            final brand = (p['brands'] ?? '').toString();
            final ingredients = (p['ingredients_text'] ?? '').toString();
            return ProductDto(
              barcode: code,
              name: name,
              brand: brand,
              category: 'Indian Food & Grocery',
              ingredientsText: ingredients,
            );
          }).where((dto) => dto.barcode.isNotEmpty).toList();
        }
      }
      return [];
    } catch (_) {
      return [];
    }
  }
}

final productApiServiceProvider = Provider<ProductApiService>((ref) {
  final dio = ref.watch(dioProvider);
  return ProductApiServiceImpl(dio);
});
