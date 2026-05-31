# MGPS Frontend - AI Instructions

## 🛠️ Stack
- **Framework**: React 18
- **Build Tool**: Vite
- **Language**: TypeScript
- **Styling**: Vanilla CSS (CSS Variables for theme)
- **API Client**: Fetch / Axios

## 🏗️ Core Principles
1. **Component Driven**: Build small, reusable components in `src/components/`.
2. **Strict Typing**: No `any`. Use interfaces from `src/types.ts`.
3. **Vanilla CSS**: Avoid Tailwind for complex layout logic to ensure high maintenance quality. Use custom properties for theming.
4. **State Management**: Prefer React Hooks and Context for shared state.

## 📝 Patterns
1. **Modules**: Each large feature is a "Module" (e.g. `FeeManagementModule`).
2. **API Layer**: Keep all API calls in `src/api.ts` or scoped service files.
3. **Error Handling**: Use the global `ApiResponse` pattern to display messages.

## 🧪 Validation
- Ensure all forms have client-side validation.
- Match backend property names exactly (e.g. `yearId` instead of `id` for Academic Years).
