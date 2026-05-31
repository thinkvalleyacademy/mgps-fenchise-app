# Coding Standards

## Backend (Java/Spring Boot)
1. **Java Version**: Always use Java 21 features (Records, Pattern Matching, etc.).
2. **Layered Architecture**: Controller -> Service -> Repository.
3. **DTOs**: Use DTOs for all API requests and responses. Never expose Entities directly.
4. **Naming**: 
   - Classes: `PascalCase`
   - Methods/Variables: `camelCase`
   - Constants: `UPPER_SNAKE_CASE`
5. **Lombok**: Use `@Data`, `@Getter`, `@Setter`, `@Builder` sparingly to reduce boilerplate.
6. **Exception Handling**: Use `@ControllerAdvice` for global exception management.

## Frontend (React/TypeScript)
1. **Functional Components**: Use Hooks (useState, useEffect, useMemo, etc.).
2. **TypeScript**: Use strict typing. Avoid `any`. Define interfaces in `types.ts`.
3. **Styling**: Prefer **Vanilla CSS** for complex components to maintain fine-grained control. Use CSS Variables for theme consistency.
4. **State Management**: Use React's built-in `useState`/`useContext` for local state.
5. **Naming**:
   - Components: `PascalCase.tsx`
   - Hooks: `useCamelCase.ts`
   - Utilities: `camelCase.ts`

## General
1. **Comments**: Write self-documenting code. Use Javadoc/TSDoc only for complex logic.
2. **Formatting**: Follow standard Prettier/Checkstyle rules.
3. **DRY**: Don't Repeat Yourself. Extract shared logic into `common` modules or utility functions.
