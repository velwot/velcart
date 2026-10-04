import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../constants/api_constants.dart';
import '../errors/app_exception.dart';

final dioProvider = Provider<Dio>((ref) {
  final dio = Dio(
    BaseOptions(
      baseUrl: ApiConstants.baseUrl,
      connectTimeout: ApiConstants.connectTimeout,
      receiveTimeout: ApiConstants.receiveTimeout,
      headers: {
        'Accept': 'application/json',
        'User-Agent': 'PureScan-Android/1.0.0',
      },
    ),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) {
        // Can inject auth token or custom headers here in future
        return handler.next(options);
      },
      onError: (DioException error, handler) {
        final mappedException = _mapDioError(error);
        return handler.reject(
          DioException(
            requestOptions: error.requestOptions,
            error: mappedException,
            message: mappedException.message,
            type: error.type,
            response: error.response,
          ),
        );
      },
    ),
  );

  return dio;
});

AppException _mapDioError(DioException error) {
  switch (error.type) {
    case DioExceptionType.connectionTimeout:
    case DioExceptionType.sendTimeout:
    case DioExceptionType.receiveTimeout:
    case DioExceptionType.connectionError:
      return NetworkFailureException(
        'Connection timed out. Please check your network connection.',
        error.error,
      );
    case DioExceptionType.badResponse:
      final statusCode = error.response?.statusCode;
      if (statusCode == 404) {
        return const ProductNotFoundException('', 'Requested resource not found.');
      }
      return NetworkFailureException(
        'Server returned error ($statusCode).',
        error.response?.data,
      );
    case DioExceptionType.cancel:
      return const UnknownErrorException('Request was cancelled.');
    default:
      return UnknownErrorException(
        error.message ?? 'An unknown network error occurred.',
        error.error,
      );
  }
}
