import { AnimatePresence, motion } from "framer-motion";
import React, { useCallback, useContext, useEffect, useMemo } from "react";
import { FaAngleDown, FaAngleRight } from "react-icons/fa";
import { NavLink, useLocation } from "react-router-dom";
import "./style.css";

// Context for managing accordion behavior
const SidebarMenuContext = React.createContext(null);

export const SidebarMenuProvider = ({ children }) => {
  const [openMenuId, setOpenMenuId] = React.useState(null);

  const toggleMenu = useCallback((menuId) => {
    setOpenMenuId((prev) => (prev === menuId ? null : menuId));
  }, []);

  const openMenu = useCallback((menuId) => setOpenMenuId(menuId), []);
  const closeMenu = useCallback(() => setOpenMenuId(null), []);
  const contextValue = useMemo(
    () => ({ openMenuId, toggleMenu, openMenu, closeMenu }),
    [openMenuId, toggleMenu, openMenu, closeMenu]
  );

  return (
    <SidebarMenuContext.Provider value={contextValue}>
      {children}
    </SidebarMenuContext.Provider>
  );
};

const SidebarMenu = ({ route, showAnimation, isOpen, setIsOpen, menuId }) => {
  const { openMenuId, toggleMenu, openMenu, closeMenu } = useContext(SidebarMenuContext);
  const isMenuOpen = openMenuId === menuId;
  const location = useLocation();
  const isRouteActive =
    Array.isArray(route.subRoutes) &&
    route.subRoutes.some((subRoute) => location.pathname === subRoute.path || location.pathname.startsWith(`${subRoute.path}/`));

  const handleToggle = () => {
    setIsOpen(true);
    toggleMenu(menuId);
  };

  useEffect(() => {
    if (!isOpen) {
      // When sidebar closes, close all menus
      if (isMenuOpen) {
        closeMenu();
      }
    }
  }, [isMenuOpen, isOpen, closeMenu]);

  useEffect(() => {
    // Keep the active submenu expanded when the sidebar is open
    if (isOpen && isRouteActive) {
      openMenu(menuId);
    }
  }, [isOpen, isRouteActive, location.pathname, menuId, openMenu]);

  return (
    <>
      <button
        type="button"
        className={isRouteActive ? "menu active" : "menu"}
        onClick={handleToggle}
        aria-expanded={isOpen && isMenuOpen}
        aria-controls={`submenu-${menuId}`}
      >
        <div className="menu_item">
          <div className="icon">{route.icon}</div>
          <AnimatePresence>
            {isOpen && (
              <motion.div
                variants={showAnimation}
                initial="hidden"
                animate="show"
                exit="hidden"
                className="link_text"
              >
                {route.name}
              </motion.div>
            )}
          </AnimatePresence>
        </div>
        {isOpen && (
          <motion.div
            animate={{ rotate: isMenuOpen ? -180 : 0 }}
            transition={{ duration: 0.25 }}
            className="menu_arrow"
          >
            <FaAngleDown />
          </motion.div>
        )}
      </button>

      <div
        id={`submenu-${menuId}`}
        className={isOpen && isMenuOpen ? "submenu submenu--open" : "submenu"}
      >
        {route.subRoutes.map((subRoute) => (
          <NavLink
            key={subRoute.path}
            to={subRoute.path}
            className={({ isActive }) => (isActive ? "submenu_link active" : "submenu_link")}
          >
            <div className="submenu_icon">
              <FaAngleRight />
            </div>
            <span className="submenu_text">{subRoute.name}</span>
          </NavLink>
        ))}
      </div>
    </>
  );
};

export default SidebarMenu;
