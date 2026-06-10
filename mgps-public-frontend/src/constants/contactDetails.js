// src/constants/contactDetails.js
export const CONTACT_DETAILS = {
    phone1: "+91 91255 40611",
    phone2: "+91 78003 96303",
    email: 'mgpssbdr01@gmail.com',
    address: 'Sahijan Khurd, Churk-Sonbhadra, Pin Code-231206',
    instagram: 'https://www.instagram.com/goosemothers_churk_road?igsh=MTgxcWNzMmp4aGs5OQ%3D%3D&utm_source=qr'
  };

  export const WHATSAPP_NUMBER = CONTACT_DETAILS.phone2.replace(/\D/g, '');

export const WHATSAPP_LINK = `https://wa.me/${WHATSAPP_NUMBER}`;
// export const WHATSAPP_LINK = `https://wa.me/${WHATSAPP_NUMBER}?text=${encodeURIComponent("Hi, I'm interested in admission details.")}`;


  