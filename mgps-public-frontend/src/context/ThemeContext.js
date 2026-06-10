// src/context/ThemeContext.js
// React Context for dynamic theming based on school branding

import React, { createContext, useContext, useMemo, useState, useEffect } from 'react';
import { ThemeProvider as MUIThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { useSchoolBranding } from '../hooks/useSchoolBranding';

const ThemeContext = createContext(null);

/**
 * Hook to access theme context
 * @returns {Object} Theme context with branding information
 */
export const useThemeContext = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useThemeContext must be used within a DynamicThemeProvider');
  }
  return context;
};

/**
 * Dynamic Theme Provider component
 * Wraps the application with MUI theme provider using school branding colors
 */
export const DynamicThemeProvider = ({ children }) => {
  const { branding, loading } = useSchoolBranding();
  const [theme, setTheme] = useState(null);

  useEffect(() => {
    if (!loading && branding) {
      const newTheme = createTheme({
        palette: {
          primary: {
            main: branding.primaryColor || '#3498db',
            light: lightenColor(branding.primaryColor || '#3498db', 20),
            dark: darkenColor(branding.primaryColor || '#3498db', 20),
          },
          secondary: {
            main: branding.secondaryColor || '#2ecc71',
            light: lightenColor(branding.secondaryColor || '#2ecc71', 20),
            dark: darkenColor(branding.secondaryColor || '#2ecc71', 20),
          },
          background: {
            default: '#f5f7fa',
            paper: '#ffffff',
          },
        },
        typography: {
          fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
          h1: {
            fontWeight: 700,
          },
          h2: {
            fontWeight: 700,
          },
          h3: {
            fontWeight: 600,
          },
          h4: {
            fontWeight: 600,
          },
          h5: {
            fontWeight: 600,
          },
          h6: {
            fontWeight: 600,
          },
          button: {
            textTransform: 'none',
            fontWeight: 600,
          },
        },
        shape: {
          borderRadius: 8,
        },
        components: {
          MuiButton: {
            styleOverrides: {
              root: {
                textTransform: 'none',
                borderRadius: 8,
                fontWeight: 600,
              },
              contained: {
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
              },
            },
          },
          MuiCard: {
            styleOverrides: {
              root: {
                borderRadius: 12,
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.07)',
              },
            },
          },
          MuiAppBar: {
            styleOverrides: {
              root: {
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
              },
            },
          },
          MuiTextField: {
            styleOverrides: {
              root: {
                '& .MuiOutlinedInput-root': {
                  borderRadius: 8,
                },
              },
            },
          },
        },
      });
      
      setTheme(newTheme);
    }
  }, [branding, loading]);

  const contextValue = useMemo(() => ({ branding, loading }), [branding, loading]);

  if (!theme) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        background: '#f5f7fa'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{ 
            width: 40, 
            height: 40, 
            border: '4px solid #3498db',
            borderTopColor: 'transparent',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            margin: '0 auto 16px'
          }} />
          <p style={{ color: '#666', margin: 0 }}>Loading {branding.schoolName}...</p>
        </div>
      </div>
    );
  }

  return (
    <ThemeContext.Provider value={contextValue}>
      <MUIThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MUIThemeProvider>
    </ThemeContext.Provider>
  );
};

/**
 * Lighten a hex color
 * @param {string} color Hex color
 * @param {number} percent Percentage to lighten
 * @returns {string} Lightened hex color
 */
const lightenColor = (color, percent) => {
  const num = parseInt(color.replace('#', ''), 16);
  const amt = Math.round(2.55 * percent);
  const R = (num >> 16) + amt;
  const G = (num >> 8 & 0x00FF) + amt;
  const B = (num & 0x0000FF) + amt;
  return '#' + (
    0x1000000 +
    (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
    (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
    (B < 255 ? (B < 1 ? 0 : B) : 255)
  ).toString(16).slice(1);
};

/**
 * Darken a hex color
 * @param {string} color Hex color
 * @param {number} percent Percentage to darken
 * @returns {string} Darkened hex color
 */
const darkenColor = (color, percent) => {
  const num = parseInt(color.replace('#', ''), 16);
  const amt = Math.round(2.55 * percent);
  const R = (num >> 16) - amt;
  const G = (num >> 8 & 0x00FF) - amt;
  const B = (num & 0x0000FF) - amt;
  return '#' + (
    0x1000000 +
    (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
    (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
    (B < 255 ? (B < 1 ? 0 : B) : 255)
  ).toString(16).slice(1);
};

export default DynamicThemeProvider;
