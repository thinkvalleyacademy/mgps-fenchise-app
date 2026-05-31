# API Guidelines

## Endpoint Structure
All APIs should follow the RESTful pattern and be versioned.
- Base URL: `/api/v1/`
- Example: `/api/v1/schools`, `/api/v1/academic/sessions`

## Request Headers
- `X-Tenant-ID`: Must be provided if authentication is not yet established or for global operations.
- `Authorization`: `Bearer <JWT_TOKEN>` for authenticated requests. The token contains the tenant context.

## Response Format
Use a consistent `ApiResponse` wrapper:
```json
{
  "success": true,
  "data": { ... },
  "message": "Resource created successfully",
  "timestamp": "2026-05-30T..."
}
```

## Error Handling
Return appropriate HTTP status codes:
- `200 OK`: Successful GET/PUT/DELETE.
- `201 Created`: Successful POST.
- `400 Bad Request`: Validation errors or business logic violations.
- `401 Unauthorized`: Missing or invalid token.
- `403 Forbidden`: Insufficient permissions.
- `404 Not Found`: Resource does not exist.
- `409 Conflict`: Duplicate resources.
- `500 Internal Server Error`: Unhandled exceptions.

Include a clear error message and optional error codes in the response body.

## Validation
- Use `@Valid` and JSR-303 annotations in the backend.
- Frontend should perform client-side validation before submission.
