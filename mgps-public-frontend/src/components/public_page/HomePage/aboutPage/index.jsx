import React from 'react'
import "./style.css"

const AboutUs = () => {
  return (
    <>
       <div id="about" className="about">
        <div
          className="responsiveContainerBlock bigContainer"
        >
          <div
            className="responsiveContainerBlock Container" style={{background:"transparent"}}
          >
            <p className="textBlk heading">About Us</p>
            <p className="textBlk subHeading">
              At Mother's Goose, we are dedicated to fostering a nurturing and
              inspiring environment where every child feels valued and
              encouraged to explore their potential. Our mission is to provide a
              safe and stimulating space where children can grow, learn, and
              thrive. We emphasize respect, creativity, integrity, and
              excellence in all our activities, aiming to instill a lifelong
              love of learning and strong moral principles in our students. By
              creating a supportive community, we ensure that each child can
              develop their unique abilities and build a strong foundation for
              their future.
            </p>
	            <div className="socialIconsContainer">
	              <button type="button" className="socialIcon" aria-label="Social link 1">
	                <img
	                  alt=""
	                  className="socialIconImageBlock"
	                  src="https://workik-widget-assets.s3.amazonaws.com/widget-assets/images/bb33.png"
	                />
	              </button>
	              <button type="button" className="socialIcon" aria-label="Social link 2">
	                <img
	                  alt=""
	                  className="socialIconImageBlock"
	                  src="https://workik-widget-assets.s3.amazonaws.com/widget-assets/images/bb34.png"
	                />
	              </button>
	              <button type="button" className="socialIcon" aria-label="Social link 3">
	                <img
	                  alt=""
	                  className="socialIconImageBlock"
	                  src="https://workik-widget-assets.s3.amazonaws.com/widget-assets/images/bb35.png"
	                />
	              </button>
	              <button type="button" className="socialIcon" aria-label="Social link 4">
	                <img
	                  alt=""
	                  className="socialIconImageBlock"
	                  src="https://workik-widget-assets.s3.amazonaws.com/widget-assets/images/bb36.png"
	                />
	              </button>
	            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export default AboutUs
