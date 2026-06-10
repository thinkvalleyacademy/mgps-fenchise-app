import React, { createContext, useState, useEffect, useContext } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('authToken') || null);
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('authUser')) || null);
  const [userName, setUserName] = useState(() => localStorage.getItem('authUserName') || null);
  const [name, setName] = useState(() => localStorage.getItem('authName') || null);

  useEffect(() => {
    if (token) {
      localStorage.setItem('authToken', token);
    } else {
      localStorage.removeItem('authToken');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('authUser', JSON.stringify(user));
    } else {
      localStorage.removeItem('authUser');
    }
  }, [user]);

  useEffect(() => {
    if (userName) {
      localStorage.setItem('authUserName', userName);
    } else {
      localStorage.removeItem('authUserName');
    }
  }, [userName]);

  useEffect(() => {
    if (name) {
      localStorage.setItem('authName', name);
    } else {
      localStorage.removeItem('authName');
    }
  }, [name]);

  const logout = () => {
    setToken(null);
    setUser(null);
    setUserName(null);
    setName(null);
    localStorage.removeItem('authToken');
    localStorage.removeItem('authUser');
    localStorage.removeItem('authUserName');
  };

  return (
    <AuthContext.Provider value={{ token, setToken, user, setUser, userName, setUserName, name, setName, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use AuthContext
export const useAuth = () => {
  return useContext(AuthContext);
};

export default AuthContext;
