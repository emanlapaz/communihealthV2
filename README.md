# communihealthV2
COMMUNIHEALTHV2.0

Youtube Link: https://youtu.be/jidXN5bsynM

<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/4683f2f4-6774-4393-9621-7f6d4c5adf94" alt="Alt text" width="200"/>


Intro:
<br>
 Communihealth is an Android Mobile App created specifically for Community Health Nurses. Home visits by community health nurses provide essential care and education directly in patients' homes. These visits are especially beneficial for elderly, disabled, or chronically ill individuals who might struggle to access traditional healthcare settings. Nurses assess health, manage conditions, offer wound care, and educate on health management, taking into account environmental and social factors impacting the patient's well-being. This approach ensures personalized, comprehensive care, promoting long-term health and reducing hospital readmissions. 

<br>
The App:

CommuniHealth uses Firebase for realtime database and photo storage. It also utilizes Google Oauth for logging in gmail users. You can also create your account which will be sent to firebase and use it to sign in. <br>
Upon successful log in, the user will be brought in to the patients list screen where you can view previously added patient and where you can create one. <br>

<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/203dad0f-5408-42cc-9390-bbc0db4da526" alt="Alt text" width="200"/><br>

The add patient screen will require the user to input the patient details as below: <br>
- First and Last Names<br>
- Date of Birth in this format dd/yy/mmmm <br>
- Eircode <br>
- If Eircode not available, the user can user the current location and mark it as patient address
- Services Required <br>

The user can also upload an image that will be used as the patient's profile photo. <br>

<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/709129a6-b455-4d55-873e-b1e1e8cb38dc" alt="Alt text" width="200"/><br>

The USE EIRCODE registers the EIRCODE as the patients address and will mark it on the map screen for navigation using the Google Maps. <br>
<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/1d6bdf85-0370-48b1-9fdc-24261b369e3f" alt="Alt text" width="200"/><br>
<img src="https://github.com/emanlapaz/communihealthV2/blob/master/app/src/main/res/drawable/7_navigate.png" alt="Alt text" width="200"/><br>

In the case that Eircode is not avaialbe the user can use current location as patient's address if the user is at the patient's address
<img src="https://github.com/emanlapaz/communihealthV2/blob/master/app/src/main/res/drawable/Screenshot%202023-12-26%20084244.png" alt="Alt text" width="200"/><br>


The Patient List screen display the patients that the user added. You can also toggle to display all the patients added by all users. The same applies to the Patient Location screen which displays the marker for all patients.<br>
Swiping left will give you the option to DELETE the patient while swiping right will EDIT the patient data. <br>
<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/0bb35fbf-783c-403f-a5bb-87272eea9c3d" alt="Alt text" width="200"/><br>

Clicking the patient on the patient list will open up the profile page which displays all the relevant data. There are also 2 buttons NAVIGATE and SCHEDULE. <br>
NAVIGATE will redirect the user to the Google MAP app passing along the EIRCODE to be searched.<br>
SCHEDULE will redirect the user to a calendar screen where the user can set up the appointment for a home visit. The appointment data (date/time) will be passed back to the profile screen.<br>

<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/73ecd4fa-fe54-4a88-bb9d-46915572c2f2" alt="Alt text" width="200"/>
<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/21762fcf-08b8-4b13-9efe-bb3fcb3891f5" alt="Alt text" width="200"/>
<br>

There is also a nav drawer for ease of access to the other screens.<br>

<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/2c49f8f9-7eb6-4fc5-ab0b-f0dfb196bd7c" alt="Alt text" width="200"/><br>

About screen<br>


<img src="https://github.com/emanlapaz/communihealthV2/assets/96552779/e013a15f-2b4a-4036-956f-e3e629933226" alt="Alt text" width="200"/><br>
<br>


Firebase:

<img src="https://github.com/emanlapaz/communihealthV2/blob/master/app/src/main/res/drawable/Screenshot%202023-12-21%20094919.png" alt="Alt text" width="200"/><br>
<br>


Class Diagrams:

<img src="https://github.com/emanlapaz/communihealthV2/blob/master/app/src/main/res/drawable/Screenshot%202023-12-26%20083757.png" alt="Alt text" width="200"/><br>
<br>

HOME Class is the main activity and extends to the AppCompactActivity. The Home class integrated various components as follows: <br>
DrawerLayout: Navigation <br>
HomeBinding and NavHeaderBinding: UI Binding <br>
AppBarConfiguration: Navigation bar setup <br>
LoggedInViewModel, MapsViewModel, and FirebaseAuth: User session and map functionality <br>
Intent: interactivity communication<br>

ProfileFragment Class is associated with ProfileViewModel for data handling and interacts with LoggedInViewModel for users. ProfileFragementArgs is used for arguement passing and FragmentProfleBinding for UI interaction. <br>
PatientDetailsViewModel manages the data logic and interacts witht eh FirebaseDBManager for database operations and also holds the Patient Model <br>
PatientListFragement implements the PatientClickListener and uses the PatientListViewModel  for data management. <br>
PatientFragment works with the FirebaseImageManager for image handling  and PatientViewModel for patient data. <br>
MapsFragment uses an AlertDialog for alerts and interacts with SupportMapFragment and OnMapReadyCallback for map functionalities. It utilizes MapsViewModel for map data handling, PatientListViewModel for patient data, and LoggedInViewModel for users.<br>
PatientDetailFragment Class uses PatientDetailViewModel for detailed patient data handling, ProfileFragmentArgs for argument passing, FragmentPatientDetailBinding for UI binding, and LoggedInViewModel.<br>



Future Development:
Communihealth can be developed in the future and be modified to add various functions like: <br>
- Appointment List<br>
- Communication EMAIL/SHARE<br>
- Patient Notes<br>
- Filtering<br>
- Name Search<br>
<br>

Personal Statement:

This android project and the Mobile App module itself is very challenging


References: <br>
SETU Lecture Materials(2023)<br>

Android Developers (2023), Navigation Component, Available at: https://developer.android.com/guide/navigation (Accessed: 12 December 2023). <br>

Android Developers (2023), LiveData Overview, Available at: https://developer.android.com/topic/libraries/architecture/livedata (Accessed: 12 December 2023). <br>

Android Developers (2023), ViewModel Overview, Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 12 December 2023). <br>

Firebase (2023), Firebase Realtime Database, Available at: https://firebase.google.com/docs/database (Accessed: 12 December 2023). <br>

Picasso (2023), Picasso Image Library, Available at: http://square.github.io/picasso/ (Accessed: 12 December 2023). <br>

Timber (2023), Timber Logging Utility, Available at: https://github.com/JakeWharton/timber (Accessed: 12 December 2023). <br>

OpenAI. (2022). ChatGPT – OpenAI Artificial Intelligence. Chatbot Business Framework. Available at: https://chatbotbusinessframework.com/chatgpt-overview/ [Accessed 22 Dec. 2023].





















 
 
