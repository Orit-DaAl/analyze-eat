# 🥗 AnalyzeEat- AI Nutritionist Assistant

**AnalyzeEat** is an intelligent nutrition analysis platform that allows users to get instant dietary insights from any recipe on the web. By leveraging the **Google Gemini AI** model, the system analyzes raw recipe text to calculate nutritional values and suggest smart, low-calorie ingredient substitutions without sacrificing portion sizes.

The project consists of a **Chrome Extension** frontend and a **Java Spring Boot** backend.

---

<div align="center">
  <h2>✨ Application Demo</h2>
  <img src="./assets/demo.gif" 
       alt="Application Demo" 
       style="max-width: 100%; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);" />
</div>

---

### 🚀 How it Works

The application provides a seamless flow from a simple text selection to a complete nutritional analysis:

1.  **Selection**: The user highlights any list of ingredients directly within a recipe website.
2.  **Capture**: The **Chrome Extension** extracts the raw text via the **Scripting API** and forwards it to the **Spring Boot** backend.
3.  **Analysis**: **Google Gemini AI** processes the ingredients to calculate a precise nutritional profile-covering **Calories, Protein, Fats, and Carbs**—calibrated according to the number of servings.
4.  **Refinement**: The system automatically suggests **Low-calory Swaps**, identifying high-calorie ingredients and providing lighter alternatives to reduce the recipe's total caloric value.

---

### ✨ Key Features

* **Intelligent Ingredient Parsing**: Leverages AI to interpret natural language measurements (e.g., "3 large spoons," "a pinch") and convert them into structured data.
* **Per-Serving Nutrition Stats**: Automatically calculates a detailed breakdown of **Calories, Protein, Fats, and Carbs** specifically adjusted to the recipe's serving size.
* **Smart Low-calory Swaps**: Proactively identifies high-calorie ingredients and suggests lighter, practical alternatives without reducing the overall portion size.
* **Seamless Side-Panel Integration**: Built as a **Chrome Side Panel**, allowing you to view analysis and original recipes side-by-side without switching tabs.

---
## 🏗️ System Architecture

The platform follows a decoupled architecture, separating data extraction from AI processing:

* **Client (Chrome Extension)**: Manages the UI via a **Chrome Side Panel** and uses the **Scripting API** to capture highlighted text directly from the browser's active tab.
* **Backend (Spring Boot)**: Acts as the logic engine, utilizing **Spring WebFlux** for asynchronous communication.
* **Communication**: The Client and Backend communicate over **REST/HTTP**, where raw recipe text is sent as a JSON payload, and the processed nutritional data is returned for dynamic rendering.
* **Intelligence Layer**: Integration with **Google Gemini API** via advanced Prompt Engineering to transform unstructured text into validated nutritional JSON.

---

## 🛠️ Tech Stack

* **Backend:** Java 21, Spring Boot 3.x, Spring WebFlux, Maven.
* **Frontend:** JavaScript (ES6+), HTML5, CSS3, Chrome API.
* **AI Engine:** Google Gemini API.

---
## 🏁 Getting Started

### 1. Project Setup
Clone the repository from GitHub and ensure you have **Java 21** and **Maven** installed on your system.

### 2. Environment Variables
To enable the AI analysis features, you must define the following variables in your operating system or your `application.yaml` file:

| Variable | Purpose |
| :--- | :--- |
| `GEMINI_API_KEY` | Your Google Gemini API Key |
| `GEMINI_API_URL` | Your Google Gemini URL |

### 3. Launching the Backend
To start the server, run the **AnalyzeEat** application through your IDE. The server will initialize at `http://localhost:8080`.

### 4. Installing the Chrome Extension
Open the Chrome Extensions page (`chrome://extensions/`), enable **Developer Mode**, and use the **"Load unpacked"** feature to upload the `analyzeeat_ext` folder. Once installed, pin the extension to your toolbar.


---

📞 Contact with me: 
**Orit Alster** - [LinkedIn Profile](https://www.linkedin.com/in/orit-alster)