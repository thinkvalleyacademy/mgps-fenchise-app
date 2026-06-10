import React from "react";
import styles from "./HomePage.module.css";
import { useNavigate } from "react-router-dom";
import CardImage from "./CardIMage.jpg";
import MyFooter from "./footerComponent";
import AboutUs from "./aboutPage";
import HomeNav from "./homeNav";
import HomeCard from "./homeCard";
import { FaUserTie } from "react-icons/fa";
import { IoShieldCheckmark } from "react-icons/io5";
import { FaRegLightbulb } from "react-icons/fa6";
import { FaHandsHelping } from "react-icons/fa";
import Slider from "./slider";

import AutoOpenModal from "../quick-register-popup/quick-registration-popup";

const HomePage = () => {
  const navigate = useNavigate();

  const NavigatePage = (pageName) => {
    console.log(pageName);
    if (pageName === "login") {
      const newTab = window.open(`${window.location.origin}/login`, "_blank");
      if (newTab) {
        newTab.focus();
      }
    } else {
      navigate(`/${pageName}`);
    }
  };

  return (
    <>
    <HomeNav isOtherOptions={true} />
      <div className={styles.HeaderPage}>
        
        <div id="section1" className={`${styles.section1Class} col-sm-12`}>
          < Slider />
            
        </div>

        <div id="section2" className={styles.section2Class}>
          <div
            className={`${styles.feat} ${styles.bgGray} ${styles.pt5} ${styles.pb5}`}
          >
            <div className="container-fluid" style={{ margin: "10px" }}>
              <div className="row">
                <div className={`${styles.sectionHead} col-sm-12`}>
                  <h4>
                    <span>Why Choose</span> Us?
                  </h4>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_one`}>
                    <FaUserTie />
                    </span>
                    <h6>Qualified Staff</h6>
                    <p>
                      Our team of dedicated educators is not only highly
                      qualified but also deeply passionate about guiding young
                      minds.
                    </p>
                  </div>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_two`}>
                    <IoShieldCheckmark />
                    </span>
                    <h6> Secure Environment</h6>
                    <p>
                      Ensuring a secure campus environment is our top priority,
                      providing parents with peace of mind.{" "}
                    </p>
                  </div>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_three`}>
                      <i className="fa fa-users"></i>
                    </span>
                    <h6>Inclusive Learning Culture</h6>
                    <p>
                      We celebrate diversity and create an inclusive atmosphere
                      where every student feels welcomed and valued.
                    </p>
                  </div>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_four`}>
                    <FaRegLightbulb />
                    </span>
                    <h6>Innovative Teaching Methods</h6>
                    <p>
                      From STEAM education to project-based learning, we embrace
                      innovative approaches that spark creativity and critical
                      thinking.
                    </p>
                  </div>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_five`}>
                    <FaHandsHelping />
                    </span>
                    <h6>Strong Partnership with Parents</h6>
                    <p>
                      Collaboration with parents is crucial to our approach,
                      ensuring that each child receives consistent support both
                      at home and in school.
                    </p>
                  </div>
                </div>
                <div className="col-lg-4 col-sm-6">
                  <div className={styles.item}>
                    {" "}
                    <span className={`${styles.icon} feature_box_col_six`}>
                      <i className="fa fa-star"></i>
                    </span>
                    <h6>Character Building</h6>
                    <p>
                      We instill essential values and character traits in our
                      students, empowering them to become responsible global
                      citizens.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <section id="programs" className={` ${styles.programs} pb-5 `}>
          <div className="container-fluid">
            <div className={`${styles.sectionHead} col-sm-12`}>
              <h4>
                <span>Our</span> Programs
              </h4>
            </div>
            <div className={`row ${styles.programsCard}`}>
              <div className="col-sm-3">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/playgroup1.webp"
                  title="Play Group"
                  hoverText="Our Playgroup program aims at developing the essential foundational 
                skills in your child, such as basic mannerisms 
                and social and language development."
                />
              </div>
              <div className="col-sm-3">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/nursey.webp"
                  title="Nursery"
                  hoverText="Our Nursery program builds on the foundational skills and 
                introduces structured learning to your child, 
                which includes early literacy and numeracy."
                />
              </div>
              <div className="col-sm-3">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/lkg.webp"
                  title="Lower KG"
                  hoverText="Our LKG program prepares your child for formal schooling with a more integrated preschool curriculum focusing on language and cognitive skills."
                />
              </div>
              <div className="col-sm-3">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/ukg.webp"
                  title="Upper KG"
                  hoverText="Our UKG program refines your child’s academic, social, and creative abilities and ensures that your child is perfectly ready for formal schooling."
                />
              </div>
            </div>
          </div>
        </section>

      </div>
      
      <AutoOpenModal />
    
      <AboutUs />

      <MyFooter />
    </>
  );
};

export default HomePage;

