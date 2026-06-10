# Gallery Images Folder Structure

## 📁 Folder Location
`mgps-sms-frontend/public/GalleryImages/`

## 📂 Category Folders

### 1. **activities/** 
For activity photos
- Art & craft sessions
- Music & dance classes
- Indoor games
- Creative workshops
- Example: `activities/art-class-01.jpg`

### 2. **classroom/**
For classroom photos
- Learning sessions
- Group activities
- Teacher-student interactions
- Study time
- Example: `classroom/learning-time-01.jpg`

### 3. **playground/**
For outdoor/playground photos
- Sports activities
- Outdoor games
- Playtime
- Physical activities
- Example: `playground/sports-day-01.jpg`

### 4. **events/**
For school events
- Annual functions
- Celebrations
- Competitions
- Special occasions
- Example: `events/annual-day-01.jpg`

### 5. **achievements/**
For achievement photos
- Award ceremonies
- Competition winners
- Certificates distribution
- Success moments
- Example: `achievements/award-ceremony-01.jpg`

### 6. **festivals/**
For festival celebrations
- Diwali celebration
- Holi festival
- Christmas party
- Cultural festivals
- Example: `festivals/diwali-2026-01.jpg`

### 7. **trips/**
For field trip photos
- Educational trips
- Picnics
- Zoo visits
- Museum tours
- Example: `trips/zoo-visit-2026-01.jpg`

---

## 📸 Image Guidelines

### Recommended Formats:
- **Photos**: JPG, PNG, WEBP
- **Videos**: MP4, WebM

### Recommended Sizes:
- **Photos**: 1920x1080px (Full HD) or 1280x720px (HD)
- **Videos**: 720p or 1080p
- **File Size**: Keep under 5MB per image for faster loading

### Naming Convention:
```
{event-name}-{sequence-number}.{extension}

Examples:
- art-class-01.jpg
- sports-day-02.jpg
- diwali-2026-01.jpg
- annual-function-03.jpg
```

---

## 🎥 Adding Videos

Create a `videos/` subfolder in each category:
```
GalleryImages/
├── events/
│   ├── photos/
│   └── videos/
│       ├── annual-day-2026.mp4
│       └── sports-day.mp4
├── activities/
│   ├── photos/
│   └── videos/
│       └── art-competition.mp4
```

---

## 📝 How to Add Images

1. **Copy your images** to the appropriate category folder
2. **Update Gallery.jsx** - Add new images to the `galleryItems` array
3. **Rebuild & Deploy** the website

### Example - Adding to Gallery.jsx:

```javascript
const galleryItems = [
  // Activities
  { 
    id: 1, 
    type: 'photo', 
    src: '/GalleryImages/activities/art-class-01.jpg', 
    alt: 'Art Class Session', 
    category: 'activities' 
  },
  
  // Events
  { 
    id: 2, 
    type: 'photo', 
    src: '/GalleryImages/events/annual-day-01.jpg', 
    alt: 'Annual Day Celebration', 
    category: 'events' 
  },
  
  // Videos
  { 
    id: 3, 
    type: 'video', 
    src: '/GalleryImages/events/videos/annual-day-2026.mp4', 
    alt: 'Annual Day 2026', 
    category: 'events' 
  },
];
```

---

## 🔄 Auto-Load Images (Future Enhancement)

To automatically load all images from folders, you can:
1. Create a JSON file listing all images
2. Or use a backend API to serve image lists
3. Or use webpack to import all images dynamically

---

## 📊 Current Folder Structure

```
GalleryImages/
├── activities/
├── classroom/
├── playground/
├── events/
├── achievements/
├── festivals/
└── trips/
```

**Total Categories: 7**

---

## ✅ Ready to Use!

Start adding your photos to the appropriate category folders!
