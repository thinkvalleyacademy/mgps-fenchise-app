import { render, screen } from '@testing-library/react';

jest.mock('axios', () => {
  const mockClient = {
    interceptors: {
      request: { use: jest.fn() },
      response: { use: jest.fn() },
    },
  };

  const mockAxios = {
    create: jest.fn(() => mockClient),
  };

  return { __esModule: true, default: mockAxios };
});

// Avoid webpack-only APIs (e.g. require.context) inside Jest by mocking the homepage route.
jest.mock('./components/public_page/HomePage/HomePageChildFriendly', () => {
  const MockHome = () => <div>Home</div>;
  return { __esModule: true, default: MockHome };
});

test('renders login screen', () => {
  const App = require('./App').default;
  window.history.pushState({}, 'Login', '/login');
  render(<App />);
  expect(screen.getByText(/sign in/i)).toBeInTheDocument();
});
