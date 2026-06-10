import React from "react";
import "./style.css";
import logo from "../Logo.png";

import { CONTACT_DETAILS,WHATSAPP_LINK } from "../../../../constants/contactDetails"


const MyFooter = () => {
  const handleScroll = (id) => {
    const element = document.getElementById(id);
    if (element) {
      window.scrollTo({
        top: element.offsetTop,
        behavior: "smooth",
      });
    }
  };
  return (
    <>
      <section className="contactArea" id="contact">
        <div className={`pt-5 pb-50 footer`}>
          <div className="container" style={{ backgroundColor: "transparent" }}>
            <div className="row">
              <div className={`col-lg-5 col-xs-12 aboutCompany`}>
                <img src={logo} className="footerImg" alt="logo" />
              </div>
              <div className={`col-lg-3 col-xs-12 links`}>
                <h2 className="mt-lg-0 mt-sm-3">Explore us</h2>
                <ul className="m-0 p-0">
                  <li>
                    -{" "}
                    <span onClick={() => handleScroll("Home")} href="/">
                      Home
                    </span>
                  </li>
                  <li>
                    -{" "}
                    <span onClick={() => handleScroll("about")} href="#">
                      About us
                    </span>
                  </li>
                  {/* <li>
                    - <span onClick={() => handleScroll("Home")} href="#">Events</span>
                  </li> */}
                </ul>
              </div>
             
                <div className={`col-lg-4 col-xs-12 location`}>
                  <h2 className="mt-lg-0 mt-sm-4">Location</h2>
                  <p>{CONTACT_DETAILS.address}</p>

                  <p className="mb-0">
                    <i className="fa fa-phone mr-3"></i>
                   Call {CONTACT_DETAILS.phone1}
                  </p>

                  <p>
                    <i className="fa fa-phone mr-3"></i>
                    <a
                      href={WHATSAPP_LINK}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ textDecoration: "none", color: "inherit" }}
                    >
                      <i className="fab fa-whatsapp me-2"></i> {CONTACT_DETAILS.phone2}

                    </a>
                  </p>

                  <p>
                    <i className="fa fa-envelope-o mr-3"></i>{CONTACT_DETAILS.email}
                  </p>

	                  <div className="map-container">
	                    <iframe
	                      title="School location map"
	                      className="map"
	                      frameBorder="0"
	                      style={{ border: 0 }}
	                      allowFullScreen
                      src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d1813.0547569239166!2d83.0610103!3d24.6543585!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x398ef900137a2047%3A0xf346ae70844d5064!2sChurk%20sahijan%20khurd!5e0!3m2!1sen!2sin!4v1724190389979!5m2!1sen!2sin"
                    />
                  </div>
                </div>

            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default MyFooter;
