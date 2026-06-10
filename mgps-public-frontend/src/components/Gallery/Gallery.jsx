import React, { useState } from 'react';
import styles from './Gallery.module.css';
import HomeNav from '../public_page/HomePage/homeNav/HomeNavChildFriendly';
import MyFooter from '../public_page/HomePage/footerComponent/MyFooterChildFriendly';
import { getAllGalleryItems, getCategoryCounts } from '../../data/galleryImages';

const Gallery = () => {
  const [selectedImage, setSelectedImage] = useState(null);
  const [activeTab, setActiveTab] = useState('all'); // all, photos, videos

  // Get all gallery items from configuration
  const galleryItems = getAllGalleryItems();
  const counts = getCategoryCounts();

  const filteredItems = activeTab === 'all' 
    ? galleryItems 
    : galleryItems.filter(item => item.type === activeTab);

  return (
    <>
      <HomeNav isOtherOptions={false} />
      
      <div className={styles.galleryPage}>
        {/* Hero Section */}
        <div className={styles.galleryHero}>
          <h1 className={styles.heroTitle}>
            <span>Our</span> Gallery 📸
          </h1>
          <p className={styles.heroSubtitle}>
            Capturing precious moments of joy, learning, and growth! ✨
          </p>
        </div>

        {/* Stats Cards */}
        <div className={styles.statsContainer}>
          <div className={styles.statCard}>
            <div className={styles.statIcon}>📷</div>
            <div className={styles.statNumber}>{counts.photos}</div>
            <div className={styles.statLabel}>Photos</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statIcon}>🎥</div>
            <div className={styles.statNumber}>{counts.videos}</div>
            <div className={styles.statLabel}>Videos</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statIcon}>🌟</div>
            <div className={styles.statNumber}>{counts.total}</div>
            <div className={styles.statLabel}>Total Memories</div>
          </div>
        </div>

        {/* Filter Tabs */}
        <div className={styles.filterTabs}>
          <button 
            className={`${styles.filterBtn} ${activeTab === 'all' ? styles.active : ''}`}
            onClick={() => setActiveTab('all')}
          >
            🌈 All
          </button>
          <button 
            className={`${styles.filterBtn} ${activeTab === 'photo' ? styles.active : ''}`}
            onClick={() => setActiveTab('photo')}
          >
            📷 Photos
          </button>
          <button 
            className={`${styles.filterBtn} ${activeTab === 'video' ? styles.active : ''}`}
            onClick={() => setActiveTab('video')}
          >
            🎥 Videos
          </button>
        </div>

        {/* Gallery Grid */}
        <div className={styles.galleryGrid}>
	          {filteredItems.map((item) => (
	            <div 
	              key={item.id} 
	              className={`${styles.galleryItem} ${styles[item.type]}`}
	              onClick={() => item.type === 'photo' && setSelectedImage(item)}
	            >
              {item.type === 'photo' ? (
                <>
                  <img 
                    src={item.src} 
                    alt={item.alt} 
                    className={styles.galleryImage}
                    loading="lazy"
                  />
                  <div className={styles.imageOverlay}>
                    <span className={styles.imageAlt}>{item.alt}</span>
                    <span className={styles.zoomIcon}>🔍</span>
                  </div>
                </>
              ) : (
                <div className={styles.videoContainer}>
                  <video 
                    src={item.src} 
                    className={styles.galleryVideo}
                    controls
                    preload="metadata"
                  >
                    Your browser does not support the video tag.
                  </video>
                  <div className={styles.videoBadge}>🎥</div>
                </div>
              )}
              <div className={styles.itemCaption}>{item.alt}</div>
            </div>
          ))}
        </div>

        {filteredItems.length === 0 && (
          <div className={styles.emptyState}>
            <div className={styles.emptyIcon}>📭</div>
            <h3>No items found</h3>
            <p>Check back soon for more memories!</p>
          </div>
        )}

        {/* Lightbox Modal for Photos */}
        {selectedImage && (
          <div className={styles.lightbox} onClick={() => setSelectedImage(null)}>
            <div className={styles.lightboxContent} onClick={(e) => e.stopPropagation()}>
              <button className={styles.closeBtn} onClick={() => setSelectedImage(null)}>✕</button>
              <img src={selectedImage.src} alt={selectedImage.alt} className={styles.lightboxImage} />
              <div className={styles.lightboxCaption}>{selectedImage.alt}</div>
              <button className={styles.navBtn} onClick={() => {
                const currentIndex = galleryItems.findIndex(item => item.id === selectedImage.id);
                const prevIndex = currentIndex > 0 ? currentIndex - 1 : galleryItems.length - 1;
                const prevItem = galleryItems.find(item => item.id === galleryItems[prevIndex].id);
                if (prevItem.type === 'photo') setSelectedImage(prevItem);
              }}>‹</button>
              <button className={styles.navBtn} onClick={() => {
                const currentIndex = galleryItems.findIndex(item => item.id === selectedImage.id);
                const nextIndex = currentIndex < galleryItems.length - 1 ? currentIndex + 1 : 0;
                const nextItem = galleryItems.find(item => item.id === galleryItems[nextIndex].id);
                if (nextItem.type === 'photo') setSelectedImage(nextItem);
              }}>›</button>
            </div>
          </div>
        )}
      </div>

      <MyFooter />
    </>
  );
};

export default Gallery;
