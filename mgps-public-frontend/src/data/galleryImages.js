// Gallery Images Configuration
// Add your images here following the folder structure
// Only add images that actually exist in the folders

export const galleryImages = {
  activities: [
    { src: '/GalleryImages/activities/wc_img1.jpeg', alt: 'Activity Session 1' },
    { src: '/GalleryImages/activities/wc_img2.jpeg', alt: 'Activity Session 2' },
    { src: '/GalleryImages/activities/wc_img3.jpeg', alt: 'Activity Session 3' },
    { src: '/GalleryImages/activities/wc_img4.jpeg', alt: 'Activity Session 4' },
    { src: '/GalleryImages/activities/wc_img5.jpeg', alt: 'Activity Session 5' },
  ],
  
  classroom: [
    // Add classroom images here when available
  ],
  
  playground: [
    // Add playground images here when available
  ],
  
  events: [
    { src: '/GalleryImages/events/wc_img1.jpeg', alt: 'Event 1' },
    { src: '/GalleryImages/events/wc_img2.jpeg', alt: 'Event 2' },
    { src: '/GalleryImages/events/wc_img3.jpeg', alt: 'Event 3' },
    { src: '/GalleryImages/events/wc_img4.jpeg', alt: 'Event 4' },
    { src: '/GalleryImages/events/wc_img5.jpeg', alt: 'Event 5' },
  ],
  
  achievements: [
    // Add achievement images here when available
  ],
  
  festivals: [
    // Add festival images here when available
  ],
  
  trips: [
    // Add trip images here when available
  ],
};

// Video Configuration
export const galleryVideos = {
  events: [
    // Add event videos here when available
    // Example: { src: '/GalleryImages/events/videos/annual-day-2026.mp4', alt: 'Annual Day 2026' },
  ],
  
  activities: [
    // Add activity videos here when available
  ],
};

// Helper function to get all gallery items
export const getAllGalleryItems = () => {
  const items = [];
  let id = 1;
  
  // Add photos from each category
  Object.entries(galleryImages).forEach(([category, images]) => {
    images.forEach(img => {
      items.push({
        id: id++,
        type: 'photo',
        src: img.src,
        alt: img.alt,
        category: category,
      });
    });
  });
  
  // Add videos from each category
  Object.entries(galleryVideos).forEach(([category, videos]) => {
    videos.forEach(vid => {
      items.push({
        id: id++,
        type: 'video',
        src: vid.src,
        alt: vid.alt,
        category: category,
      });
    });
  });
  
  return items;
};

// Helper function to get count per category
export const getCategoryCounts = () => {
  const counts = {
    photos: 0,
    videos: 0,
    total: 0,
    byCategory: {}
  };
  
  // Count photos
  Object.entries(galleryImages).forEach(([category, images]) => {
    counts.byCategory[category] = images.length;
    counts.photos += images.length;
  });
  
  // Count videos
  Object.entries(galleryVideos).forEach(([category, videos]) => {
    counts.videos += videos.length;
    if (counts.byCategory[category]) {
      counts.byCategory[category] += videos.length;
    }
  });
  
  counts.total = counts.photos + counts.videos;
  
  return counts;
};
