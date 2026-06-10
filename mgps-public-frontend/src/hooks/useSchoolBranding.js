// src/hooks/useSchoolBranding.js
// Custom hook to fetch and manage school branding for white-label support

import { useState, useEffect } from 'react';
import apiClient from '../apis/apiClient';

/**
 * Hook to fetch and manage school branding configuration
 * @returns {Object} Branding configuration, loading state, and error
 */
export const useSchoolBranding = () => {
  const [branding, setBranding] = useState({
    schoolName: 'Loading...',
    schoolCode: '',
    tagline: '',
    contactEmail: '',
    contactPhone: '',
    address: '',
    primaryColor: '#3498db',
    secondaryColor: '#2ecc71',
    logoUrl: '/default-logo.png',
    faviconUrl: '/default-favicon.ico',
    socialMediaLinks: {},
    websiteUrl: '',
    establishedYear: null,
    affiliationNumber: ''
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchBranding = async () => {
      try {
        const schoolCode = extractSchoolCodeFromDomain();
        
        const response = await apiClient.get('/api/v1/school/branding', {
          params: { schoolCode }
        });
        
        if (response.data && response.data.data) {
          const data = response.data.data;
          setBranding({
            schoolName: data.schoolName || 'School',
            schoolCode: data.schoolCode || '',
            tagline: data.tagline || '',
            contactEmail: data.contactEmail || '',
            contactPhone: data.contactPhone || '',
            address: data.address || '',
            primaryColor: data.primaryColor || '#3498db',
            secondaryColor: data.secondaryColor || '#2ecc71',
            logoUrl: data.logoUrl || '/default-logo.png',
            faviconUrl: data.faviconUrl || '/default-favicon.ico',
            socialMediaLinks: data.socialMediaLinks ? JSON.parse(data.socialMediaLinks) : {},
            websiteUrl: data.websiteUrl || '',
            establishedYear: data.establishedYear,
            affiliationNumber: data.affiliationNumber || ''
          });
          
          // Update document title
          document.title = `${data.schoolName || 'School'} - Management System`;
          
          // Update favicon
          updateFavicon(data.faviconUrl || '/default-favicon.ico');
        }
        
        setLoading(false);
      } catch (err) {
        console.error('Error fetching school branding:', err);
        setError(err);
        setLoading(false);
      }
    };

    fetchBranding();
  }, []);

  return { branding, loading, error };
};

/**
 * Extract school code from domain/subdomain
 * @returns {string} School code
 */
const extractSchoolCodeFromDomain = () => {
  const host = window.location.hostname;
  const parts = host.split('.');
  
  // For subdomain: mgps.mgps.com -> mgps
  if (parts.length > 2) {
    return parts[0].toUpperCase();
  }
  
  // For localhost or custom domain, use environment variable
  return process.env.REACT_APP_DEFAULT_SCHOOL_CODE || 'MGPS';
};

/**
 * Update favicon dynamically
 * @param {string} faviconUrl URL to the favicon
 */
const updateFavicon = (faviconUrl) => {
  let link = document.querySelector("link[rel~='icon']");
  if (!link) {
    link = document.createElement('link');
    link.rel = 'icon';
    document.head.appendChild(link);
  }
  link.href = faviconUrl;
};

export default useSchoolBranding;
