**General Information**

Project Name: Travewhere
Course: COSC2657 Android Development
Lecturer: Mr. Minh Vu Thanh
Author:
    - Dao Bao Duy: s3978826
    - Tran Nguyen Anh Minh: s3979367
    - Tran Luu Quang Tung: s3978481

------------------------------------------------------------
**Project Description**

1. Overview:
    TraveWhere is a comprehensive accommodation rental application designed to enhance the travel experience by connecting travelers with the perfect accommodations for their journeys. Whether users are planning a relaxing vacation, an adventurous getaway, or a business trip, TraveWhere offers seamless booking options, personalized features, and user-centric design to cater to diverse needs.

2. Key Features:
    - User Roles:
        + Accommodation Owners: Register and manage their properties, including room listings, pricing, and availability.
        + Travelers (Customers): Search for and book accommodations based on location, preferences, and budget.

    - Accommodation Listings:
        + Property Details: View detailed information about each accommodation, including photos, descriptions, amenities, and reviews.
        + Search Filters: Filter search results by location, price range, room type, and amenities to find the perfect accommodation.

    - Booking System:
        + Availability Calendar: Check room availability and book accommodations for specific dates.
        + Booking Confirmation: Receive instant booking confirmation and payment details after completing a reservation.
        + Secure Payment: Pay for accommodations using secure payment methods, including credit cards and digital wallets.

    - Localization and Personalization:
        + Language Support: Support English and Vietnamese languages for a personalized user experience.
        + Preferences: Light/Dark/System Default theme options for user interface customization.

    - Promotions & Discounts:
        + Special Offers: Receive notifications about exclusive promotions, discounts, and deals on accommodations.
        + Loyalty Program: Earn rewards and discounts for booking accommodations.

    - Interactive Map Integration:
        + Location Services: View accommodations on an interactive map to find properties based on search filters.
        + Directions: Get directions to accommodations from the user's current location using GPS services.

    - Secure Authentication:
        + Account registration and login for both users and accommodation owners.

    - Platform Description & Customer Support:
        + Help Center: Access FAQs, contact customer support, and report issues for assistance.
        + About Us: Learn more about the TraveWhere team, mission, and values.

3. Technical Specifications
    - Platform:
        + Android application developed in Java using Android Studio.
        + Samsung S23 Ultra emulator used for testing and development.

    - Database:
        + Powered by Firebase for real-time database and authentication.

    - Theme and Localization:
        + Dynamic Light/Dark/System Default themes for visual preferences.
        + Multi-language support managed through a LanguageHelper.

    - Google Services:
        + Google Maps SDK for Android for interactive location-based features.

    - User Interface:
        + Built with Material Design Components for a modern and intuitive UI.
        + Customizable font styles and seamless transitions.

    - SharedPreferences:
        + Persistent user settings for language, and theme preferences.

------------------------------------------------------------
**Open Issues & Known Bugs That Have Not Been Fixed**
1. Language Selection: The SettingsFragment does not update the language immediately after changing the language preference. Users need to tap onto the Setting tab to apply the new language settings or navigate to different views and come back later to apply the changes.
2. Theme Selection: Not all the colours and styles are updated when changing the theme. The theme change is not applied to the entire application, and some components may not be updated accordingly due to primary colours and the relevant components need contrast to display.