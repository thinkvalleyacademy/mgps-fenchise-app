# Known Issues & Technical Debt

## Backend
- [ ] **Data Source Leak**: Monitor connection pool sizing for multi-tenancy.
- [ ] **Schema Migration**: 2
Automate tenant schema creation on school onboarding.

## Frontend
- [ ] **Prop Drilling**: `schoolId` is passed deep into modules; consider a `SchoolContext`.
- [ ] **Mobile Responsiveness**: Sidebar needs a toggle for smaller screens.

## Infrastructure
- [ ] **JWT Rotation**: Implement refresh token mechanism.
- [ ] **SSL**: Development setup is currently HTTP.
gement within the franchise dashboard.
- **Status**: Active.

## 2026-05-30: Styling Approach
- **Decision**: Vanilla CSS with CSS Variables.
- **Rationale**: Prevents dependency on heavy utility frameworks for unique UI components, allowing better design fidelity.
- **Status**: Standardized.
