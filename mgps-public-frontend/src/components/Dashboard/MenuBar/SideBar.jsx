import { NavLink } from "react-router-dom";
import { FaBars, FaHome, FaGraduationCap, FaClipboardList, FaUser, FaSignOutAlt, FaUserCheck } from "react-icons/fa";
import { MdAccountBalance, MdLiveTv } from "react-icons/md";
import "./style.css";
import { SiMicrosoftacademic } from "react-icons/si";
import { useCallback, useContext, useEffect, useState } from "react";
import { FaUserPlus } from "react-icons/fa6";
import AuthContext from "../../../context/student/AuthContext";
import { AnimatePresence, motion } from "framer-motion";
import SidebarMenu, { SidebarMenuProvider } from "./SidebarMenu";

const SideBar = ({ children }) => {
  const { user, logout } = useContext(AuthContext);
  const [routeList, setRouteList] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  const toggle = () => setIsOpen(!isOpen);

  // Normalize user type to lowercase for comparison
  const normalizedUser = user ? user.toLowerCase() : null;

  const getFilteredRoutes = useCallback(() => {
    if (normalizedUser === "superadmin") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        { path: "/admin/approvals", name: "Approvals", icon: <FaUserCheck /> },
        { path: "/admin/onboarding-dashboard", name: "Onboarding Mgmt", icon: <FaClipboardList /> },
        { path: "/admin/client-onboarding", name: "Client Onboarding", icon: <FaGraduationCap /> },
        { path: "/Admin/seed-default-data", name: "Seed Default Data", icon: <FaClipboardList /> },
        {
          path: "/users",
          name: "Users",
          icon: <FaUser />,
          subRoutes: [
            { path: "/users/student", name: "Student" },
            { path: "/users/teacher", name: "Teacher" },
            { path: "/users/teacher-permission", name: "Teacher Permission" },
            { path: "/users/parent", name: "Parent" },
            { path: "/users/accountant", name: "Accountant" },
            { path: "/users/librarian", name: "Librarian" },
          ],
        },
        {
          path: "/registration",
          name: "Registration",
          icon: <FaUserPlus />,
          exact: true,
          subRoutes: [
            { path: "/registration/student", name: "Student" },
            { path: "/registration/teacher", name: "Teacher" },
            { path: "/registration/Admin", name: "Admin" },
          ],
        },
        {
          path: "/Alumni",
          name: "Alumni",
          icon: <FaGraduationCap />,
          subRoutes: [
            { path: "/Alumni/Manage", name: "Manage Alumni" },
            { path: "/Alumni/Fallary", name: "Gallery" },
            { path: "/Alumni/Events", name: "Events" },
          ],
        },
        {
          path: "/Academics",
          name: "Academics",
          icon: <SiMicrosoftacademic />,
          exact: true,
          subRoutes: [
            { path: "/Academics/Routine", name: "Class Routine" },
            { path: "/Academics/subject", name: "Subject" },
            { path: "/Academics/class", name: "Class" },
            { path: "/Academics/class-room", name: "Class room" },
            { path: "/Academics/department", name: "Department" },
          ],
        },
        {
          path: "/Live-Class",
          name: "Live class",
          icon: <MdLiveTv />,
          exact: true,
          subRoutes: [{ path: "/Live-Class/setting", name: "Live class setting" }],
        },
        {
          path: "/Examination",
          name: "Examination",
          icon: <FaClipboardList />,
          exact: true,
          subRoutes: [
            { path: "/Examination/Marks", name: "Marks" },
            { path: "/Examination/Grades", name: "Grades" },
            { path: "/Examination/Promotion", name: "Promotion" },
          ],
        },
        {
          path: "/Accounting",
          name: "Accounting",
          icon: <MdAccountBalance />,
          exact: true,
          subRoutes: [
            { path: "/Accounting/overview", name: "Overview" },
            { path: "/Accounting/feeDetail", name: "Fee Details" },
            { path: "/Accounting/feeReport", name: "Fee Reports" },
            { path: "/Accounting/fee-structure", name: "Fee Structure" },
            { path: "/Accounting/fee-collection", name: "Fee Collection" },
            { path: "/Accounting/invoice", name: "Invoice" },
          ],
        },
      ]);
    }

    if (normalizedUser === "admin") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        {
          path: "/users",
          name: "Users",
          icon: <FaUser />,
          subRoutes: [
            { path: "/users/student", name: "Student" },
            { path: "/users/teacher", name: "Teacher" },
            { path: "/users/teacher-permission", name: "Teacher Permission" },
            { path: "/users/parent", name: "Parent" },
            { path: "/users/accountant", name: "Accountant" },
            { path: "/users/librarian", name: "Librarian" },
          ],
        },
        {
          path: "/registration",
          name: "Registration",
          icon: <FaUserPlus />,
          exact: true,
          subRoutes: [
            { path: "/registration/student", name: "Student" },
            { path: "/registration/teacher", name: "Teacher" },
            { path: "/registration/Admin", name: "Admin" },
          ],
        },
        {
          path: "/Alumni",
          name: "Alumni",
          icon: <FaGraduationCap />,
          subRoutes: [
            { path: "/Alumni/Manage", name: "Manage Alumni" },
            { path: "/Alumni/Fallary", name: "Gallery" },
            { path: "/Alumni/Events", name: "Events" },
          ],
        },
        {
          path: "/Academics",
          name: "Academics",
          icon: <SiMicrosoftacademic />,
          exact: true,
          subRoutes: [
            { path: "/Academics/Routine", name: "Class Routine" },
            { path: "/Academics/subject", name: "Subject" },
            { path: "/Academics/class", name: "Class" },
            { path: "/Academics/class-room", name: "Class room" },
            { path: "/Academics/department", name: "Department" },
          ],
        },
        {
          path: "/Live-Class",
          name: "Live class",
          icon: <MdLiveTv />,
          exact: true,
          subRoutes: [{ path: "/Live-Class/setting", name: "Live class setting" }],
        },
        {
          path: "/Examination",
          name: "Examination",
          icon: <FaClipboardList />,
          exact: true,
          subRoutes: [
            { path: "/Examination/Marks", name: "Marks" },
            { path: "/Examination/Grades", name: "Grades" },
            { path: "/Examination/Promotion", name: "Promotion" },
          ],
        },
        {
          path: "/Accounting",
          name: "Accounting",
          icon: <MdAccountBalance />,
          exact: true,
          subRoutes: [
            { path: "/Accounting/fee-collection", name: "Fee Collection" },
            { path: "/Accounting/invoice", name: "Invoice" },
          ],
        },
      ]);
    }

    if (normalizedUser === "teacher") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        {
          path: "/users",
          name: "Users",
          icon: <FaUser />,
          subRoutes: [
            { path: "/users/student", name: "Student" },
            { path: "/users/teacher", name: "Teacher" },
          ],
        },
        {
          path: "/Academics",
          name: "Academics",
          icon: <SiMicrosoftacademic />,
          exact: true,
          subRoutes: [
            { path: "/Academics/Routine", name: "Class Routine" },
            { path: "/Academics/subject", name: "Subject" },
            { path: "/Academics/class", name: "Class" },
          ],
        },
        {
          path: "/Live-Class",
          name: "Live class",
          icon: <MdLiveTv />,
          exact: true,
          subRoutes: [{ path: "/Live-Class/setting", name: "Live class setting" }],
        },
        {
          path: "/Examination",
          name: "Examination",
          icon: <FaClipboardList />,
          exact: true,
          subRoutes: [
            { path: "/Examination/Marks", name: "Marks" },
            { path: "/Examination/Grades", name: "Grades" },
          ],
        },
      ]);
    }

    if (normalizedUser === "student") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        {
          path: "/users",
          name: "Users",
          icon: <FaUser />,
          subRoutes: [{ path: "/users/student", name: "Student" }],
        },
        {
          path: "/Academics",
          name: "Academics",
          icon: <SiMicrosoftacademic />,
          exact: true,
          subRoutes: [
            { path: "/Academics/Routine", name: "Class Routine" },
            { path: "/Academics/subject", name: "Subject" },
          ],
        },
        {
          path: "/Examination",
          name: "Examination",
          icon: <FaClipboardList />,
          exact: true,
          subRoutes: [
            { path: "/Examination/Marks", name: "Marks" },
            { path: "/Examination/Grades", name: "Grades" },
          ],
        },
      ]);
    }

    if (normalizedUser === "parent") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        {
          path: "/users",
          name: "Users",
          icon: <FaUser />,
          subRoutes: [{ path: "/users/student", name: "Student" }],
        },
        {
          path: "/Academics",
          name: "Academics",
          icon: <SiMicrosoftacademic />,
          exact: true,
          subRoutes: [
            { path: "/Academics/Routine", name: "Class Routine" },
            { path: "/Academics/subject", name: "Subject" },
          ],
        },
        {
          path: "/Examination",
          name: "Examination",
          icon: <FaClipboardList />,
          exact: true,
          subRoutes: [
            { path: "/Examination/Marks", name: "Marks" },
            { path: "/Examination/Grades", name: "Grades" },
          ],
        },
      ]);
    }

    if (normalizedUser === "accountant") {
      return setRouteList([
        { path: "/home", name: "Dashboard", icon: <FaHome /> },
        {
          path: "/Accounting",
          name: "Accounting",
          icon: <MdAccountBalance />,
          exact: true,
          subRoutes: [
            { path: "/Accounting/overview", name: "Overview" },
            { path: "/Accounting/feeDetail", name: "Fee Details" },
            { path: "/Accounting/feeReport", name: "Fee Reports" },
            { path: "/Accounting/fee-collection", name: "Fee Collection" },
            { path: "/Accounting/invoice", name: "Invoice" },
          ],
        },
      ]);
    }

    if (normalizedUser === "librarian") {
      return setRouteList([{ path: "/home", name: "Dashboard", icon: <FaHome /> }]);
    }

    return setRouteList([]);
  }, [normalizedUser]);

  useEffect(() => {
    getFilteredRoutes();
  }, [getFilteredRoutes]);

  const showAnimation = {
    hidden: { width: 0, opacity: 0, transition: { duration: 0.2 } },
    show: { opacity: 1, width: "auto", transition: { duration: 0.2 } },
  };

  const handleResize = () => {
    setWindowWidth(window.innerWidth);
  };

  useEffect(() => {
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const getSidebarWidth = () => {
    if (windowWidth <= 500) {
      return isOpen ? "280px" : "60px";
    } else if (windowWidth <= 700) {
      return isOpen ? "280px" : "60px";
    } else if (windowWidth <= 1024) {
      return isOpen ? "260px" : "60px";
    } else {
      return isOpen ? "260px" : "60px";
    }
  };

  const handleLogout = () => {
    logout();
    window.location.href = "/login";
  };

  return (
    <div className="main-container">
      <motion.div
        animate={{
          width: getSidebarWidth(),
          transition: { duration: 0.25, ease: "easeInOut" },
        }}
        className="sidebar"
      >
        {/* Logo Section with Toggle - Always visible */}
        <div className="top_section">
          <AnimatePresence>
            {isOpen && (
              <motion.h1
                variants={showAnimation}
                initial="hidden"
                animate="show"
                exit="hidden"
                className="logo"
              >
                Navigation
              </motion.h1>
            )}
          </AnimatePresence>
          <div className="bars" onClick={toggle} title={isOpen ? "Collapse" : "Expand"}>
            <FaBars />
          </div>
        </div>

        {/* User Info Section - Only when open */}
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.2 }}
            className="user-info-section"
          >
            <div className="user-avatar">
              {user ? user.charAt(0).toUpperCase() : "U"}
            </div>
            <div className="user-details">
              <span className="user-type">{user || "User"}</span>
              <span className="user-role-label">Role</span>
            </div>
          </motion.div>
        )}

        {/* Navigation Routes */}
        <div className="routes">
          <SidebarMenuProvider>
            {routeList &&
              routeList.map((route, index) => {
                if (route.subRoutes) {
                  return (
                    <SidebarMenu
                      key={index}
                      menuId={index}
                      setIsOpen={setIsOpen}
                      route={route}
                      showAnimation={showAnimation}
                      isOpen={isOpen}
                    />
                  );
                }
                return (
                  <NavLink
                    to={route.path}
                    key={index}
                    className={({ isActive }) => (isActive ? "link active" : "link")}
                  >
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
                  </NavLink>
                );
              })}
          </SidebarMenuProvider>
        </div>

        {/* Logout Section */}
        <div className="logout-section">
          <NavLink to="#" onClick={handleLogout} className="link logout-link">
            <div className="icon">
              <FaSignOutAlt />
            </div>
            <AnimatePresence>
              {isOpen && (
                <motion.div
                  variants={showAnimation}
                  initial="hidden"
                  animate="show"
                  exit="hidden"
                  className="link_text"
                >
                  Logout
                </motion.div>
              )}
            </AnimatePresence>
          </NavLink>
        </div>
      </motion.div>

      <main>{children}</main>
    </div>
  );
};

export default SideBar;
