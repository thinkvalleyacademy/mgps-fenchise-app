import React from "react";
import styles from "./ChildFriendly.module.css";
import { useNavigate } from "react-router-dom";
import MyFooter from "./footerComponent/MyFooterChildFriendly";
import AboutUs from "./aboutPage";
import HomeNav from "./homeNav/HomeNavChildFriendly";
import HomeCard from "./homeCard";
import { IoShieldCheckmark } from "react-icons/io5";
import { FaRegLightbulb } from "react-icons/fa";
import { FaHandsHelping } from "react-icons/fa";
import { FaBaby } from "react-icons/fa";
import { MdToys, MdOutlineEmojiEmotions } from "react-icons/md";
import Slider from "./slider";
import AutoOpenModal from "../quick-register-popup/quick-registration-popup";

const HomePage = () => {
  const navigate = useNavigate();

  const NavigatePage = (pageName) => {
    if (pageName === "login") {
      const newTab = window.open(`${window.location.origin}/login`, "_blank");
      if (newTab) {
        newTab.focus();
      }
    } else if (pageName === "registration" || pageName === "enroll") {
      // Trigger enquiry popup for all enrollment links
      const modalEvent = new CustomEvent('openEnquiryModal');
      window.dispatchEvent(modalEvent);
    } else if (pageName === "contact") {
      // Scroll to footer (contact section)
      const footerElement = document.getElementById('contact');
      if (footerElement) {
        footerElement.scrollIntoView({ behavior: 'smooth' });
      }
    } else {
      navigate(`/${pageName}`);
    }
  };

  return (
    <>
      <HomeNav isOtherOptions={true} />
      
      <div className={styles.HeaderPage}>
        {/* Hero Top Banner */}
        <div id="section1" className={`${styles.section1Class} col-sm-12`}>
          <div className={styles.heroTopBanner}>
            <h2 className={styles.heroTopTitle}>
              🎉 Admission Open for Session 2026-2027 🎉
            </h2>
            <p className={styles.heroTopSubtitle}>
              We welcome children to begin their learning journey at Mother&apos;s Goose Preschool!
            </p>
            <button
              type="button"
              className={styles.enrollLinkButton}
              onClick={() => NavigatePage('enroll')}
            >
              Enroll Now →
            </button>
          </div>
          
          <Slider />
          
          {/* Hero Description */}
          <div style={{ 
            maxWidth: '800px', 
            margin: '25px auto 0', 
            textAlign: 'center',
            background: 'rgba(255,255,255,0.9)',
            padding: '20px',
            borderRadius: '25px',
            boxShadow: '0 10px 30px rgba(0,0,0,0.1)'
          }}>
            <p style={{ fontSize: '1.1rem', lineHeight: '1.7', color: '#333' }}>
              At Mother's Goose Preschool, we provide a safe, caring, and inspiring environment where children 
              develop strong learning habits, confidence, creativity, and social skills. 🌟
            </p>
            <div style={{ marginTop: '15px', display: 'flex', gap: '10px', justifyContent: 'center', flexWrap: 'wrap' }}>
              <span style={{ padding: '6px 15px', background: '#4ECDC4', color: 'white', borderRadius: '20px', fontWeight: '600', fontSize: '0.9rem' }}>🌱 Complete Development</span>
              <span style={{ padding: '6px 15px', background: '#45B7D1', color: 'white', borderRadius: '20px', fontWeight: '600', fontSize: '0.9rem' }}>🏫 Green Environment</span>
              <span style={{ padding: '6px 15px', background: '#FFD93D', color: '#333', borderRadius: '20px', fontWeight: '600', fontSize: '0.9rem' }}>👩‍🏫 Experienced Teachers</span>
              <span style={{ padding: '6px 15px', background: '#FF6B6B', color: 'white', borderRadius: '20px', fontWeight: '600', fontSize: '0.9rem' }}>📹 Safe Campus</span>
            </div>
          </div>
        </div>

        {/* Why Choose Us Section - Compact */}
        <div id="section2" className={styles.section2Class}>
          <div className={`${styles.feat} ${styles.bgGray} ${styles.pt5} ${styles.pb5}`}>
            <div className="container-fluid" style={{ margin: "10px" }}>
              <div className={`${styles.sectionHead} col-sm-12`}>
                <h4 className={styles.playfulTitle}>
                  <span>Why Choose</span> Mother's Goose? 🎉
                </h4>
              </div>
              
              <div className="row">
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_one}`}>
                      <FaBaby />
                    </span>
                    <h6>Loving Teachers 👩‍🏫</h6>
                    <p>Our caring teachers treat every child like family with patience and kindness!</p>
                  </div>
                </div>
                
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_two}`}>
                      <IoShieldCheckmark />
                    </span>
                    <h6>Super Safe Campus 🛡️</h6>
                    <p>Your little ones are always safe with our CCTV cameras and secure environment.</p>
                  </div>
                </div>
                
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_three}`}>
                      <MdOutlineEmojiEmotions />
                    </span>
                    <h6>Happy Friends 👫</h6>
                    <p>Children learn to share, care, and make lifelong friends in our inclusive family.</p>
                  </div>
                </div>
                
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_four}`}>
                      <FaRegLightbulb />
                    </span>
                    <h6>Fun Learning 💡</h6>
                    <p>Creative activities, games, and projects that make learning an adventure!</p>
                  </div>
                </div>
                
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_five}`}>
                      <FaHandsHelping />
                    </span>
                    <h6>Parent Partnership 🤝</h6>
                    <p>We work together with parents to support every child's unique journey.</p>
                  </div>
                </div>
                
                <div className="col-lg-4 col-md-6">
                  <div className={styles.item}>
                    <span className={`${styles.icon} ${styles.feature_box_col_six}`}>
                      <MdToys />
                    </span>
                    <h6>Play & Grow 🎈</h6>
                    <p>Building character, confidence, and curiosity through playful learning.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          {/* Rainbow Divider */}
          <div className={styles.rainbowDivider}></div>
        </div>

        {/* Programs Section - Compact Cards */}
        <section id="programs" className={` ${styles.programs} pb-5 `}>
          <div className="container-fluid">
            <div className={`${styles.sectionHead} col-sm-12`} style={{marginTop: '20px'}}>
              <h4 className={styles.playfulTitle}>
                <span>Our</span> Fun Programs 🎨
              </h4>
            </div>
            <div className={`row ${styles.programsCard}`}>
              <div className="col-lg-3 col-md-4 col-sm-6">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/playgroup1.webp"
                  title="🧸 Play Group"
                  hoverText="A magical world of play where your little ones discover colors, shapes, and sounds! Ages 2-3 years."
                />
              </div>
              <div className="col-lg-3 col-md-4 col-sm-6">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/nursey.webp"
                  title="🌟 Nursery"
                  hoverText="Exciting adventures in learning! Letters, numbers, stories, and creative activities. Ages 3-4 years."
                />
              </div>
              <div className="col-lg-3 col-md-4 col-sm-6">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/lkg.webp"
                  title="📚 Lower KG"
                  hoverText="Big kid learning begins! Reading, writing, math fun, and science discoveries. Ages 4-5 years."
                />
              </div>
              <div className="col-lg-3 col-md-4 col-sm-6">
                <HomeCard
                  imageSrc="https://www.bachpanglobal.com/img/ukg.webp"
                  title="🎓 Upper KG"
                  hoverText="Ready for school! Master essential skills and feel confident for the big school journey. Ages 5-6 years."
                />
              </div>
            </div>
          </div>
        </section>

        {/* Call to Action - Compact */}
        <section className={styles.aboutSection}>
          <div style={{ textAlign: 'center', color: 'white' }}>
            <h2 style={{ fontSize: '2.2rem', marginBottom: '15px', fontWeight: 'bold' }}>
              🎉 Admission Open for Session 2026-2027 🎉
            </h2>
            <p style={{ fontSize: '1.15rem', marginBottom: '20px' }}>
              We welcome children to begin their learning journey at Mother's Goose Preschool!
            </p>
            <div style={{ marginBottom: '20px', fontSize: '1rem' }}>
              <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', flexWrap: 'wrap' }}>
                <span style={{ padding: '8px 18px', background: 'rgba(255,255,255,0.2)', borderRadius: '25px', fontSize: '0.9rem' }}>🧸 Playgroup: 2-3 yrs</span>
                <span style={{ padding: '8px 18px', background: 'rgba(255,255,255,0.2)', borderRadius: '25px', fontSize: '0.9rem' }}>🌟 Nursery: 3-4 yrs</span>
                <span style={{ padding: '8px 18px', background: 'rgba(255,255,255,0.2)', borderRadius: '25px', fontSize: '0.9rem' }}>📚 LKG: 4-5 yrs</span>
                <span style={{ padding: '8px 18px', background: 'rgba(255,255,255,0.2)', borderRadius: '25px', fontSize: '0.9rem' }}>🎓 UKG: 5-6 yrs</span>
              </div>
            </div>
            <button 
              className={styles.funButton}
              onClick={() => NavigatePage('enroll')}
            >
              Enroll Your Child Today! 🚀
            </button>
            <p style={{ marginTop: '15px', fontSize: '0.95rem', opacity: 0.9 }}>
              📞 Contact us or visit our campus for admission inquiries
            </p>
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
