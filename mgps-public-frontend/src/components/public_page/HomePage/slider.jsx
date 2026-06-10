import React, { useState, useEffect } from "react";
import styles from "./HomePage.module.css";

// Dynamically load all images from the folder
const importAll = (r) => r.keys().map(r);
const images = importAll(require.context("./welcomeImages", false, /\.(png|jpeg|webp)$/));

const Slider = () => {
    const [currentImage, setCurrentImage] = useState(images[0]);

    useEffect(() => {
        const interval = setInterval(() => {
        setCurrentImage((prevImage) => {
            const nextIndex = (images.indexOf(prevImage) + 1) % images.length;
            return images[nextIndex];
        });
        }, 5000); // Change image every 5 seconds

        return () => clearInterval(interval);
    }, []);

    return (
        <>
        <div style=
            {{ 
                position: "relative",
                width: "100%",
                height: "100vh",
                backgroundImage: `url(${currentImage})`,
                backgroundRepeat: "no-repeat",
                backgroundSize: "cover",
                backgroundPosition: "center",
                transition: "background-image 1s ease-in-out",
           }}
           >
            <div className={styles.overlay}> </div>
            <div className={styles.content}>
              <div
                className={styles.card}
                style={{ background: "transparent", border: "none" }}
              >
                <div className={styles["card-body"]}>
                  <h3>Welcome to</h3>
                  <h1>Mother's Goose Preschool</h1>
                  <h4 style={{ fontStyle: "italic" }}>
                    @ Future Skill Education
                  </h4>
                  <button
                    className={styles.startedButton}
                    //TODO: call Registration pop-up on click of lets get started
                  >
                    Let's Get Started
                  </button>
                </div>
              </div>
            </div>

           </div>
</>
    );
}

export default Slider;