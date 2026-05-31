# Known Issues & Technical Debt

## Backend
- [ ] **Data Source Leak**: Monitor connection pool sizing for multi-tenancy.
- [ ] **Schema Migration**: Automate tenant schema creation on school onboarding.
- [x] **Validation**: Enhance global exception handler to return field-specific validation errors. (Basic validation for schoolId added to AcademicStructureService).

## Frontend
- [ ] **Prop Drilling**: `schoolId` is passed deep into modules; consider a `SchoolContext`.
- [ ] **Mobile Responsiveness**: Sidebar needs a toggle for smaller screens.
- [ ] **UI Polish**: Standardize table headers and action button sizing across all modules.

## Infrastructure
- [ ] **JWT Rotation**: Implement refresh token mechanism.
- [ ] **SSL**: Development setup is currently HTTP.
- [ ] **CI/CD**: Initial pipeline for backend Maven build is missing.
