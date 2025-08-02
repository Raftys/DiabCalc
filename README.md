# DiabCalc  (BETA)

**DiabCalc** is a comprehensive Java application designed to empower people with diabetes by simplifying meal planning and insulin dosage calculations.

## ✨ Key Features

### 🍎 Food Database Integration
- **Searchable database** of common foods with detailed nutritional info (carbs, fats, proteins)
- **Custom food/meal creation** - Add personal recipes or frequently eaten meals
- **Favorites system** - Bookmark frequently used foods for quick access

### 🧮 Intelligent Calculation Tools
| Feature | Description |
|---------|-------------|
| Carb Counting Assistant | Automatically calculates total carbs per meal |
| Insulin Suggestion | Recommends units based on your personal ratios |
| Meal History | Tracks previous calculations for reference |

### ⚙️ Personalized Settings
- **Customizable ratios**:
  - Insulin-to-Carb (I:C) ratio
  - Insulin Sensitivity Factor (ISF)
  - Target blood glucose range
- **Dose rounding** options
- **Dark/Light mode** (if implemented)

## 🛠️ Technologies Used

| Category | Technologies |
|----------|--------------|
| **Core** | Java 11+, JavaFX |
| **Build** | Maven, GitHub Actions |
| **Testing** | JUnit 5, Mockito |
| **CI/CD** | GitHub Actions |
| **UI** | CSS, Scene Builder |

## 📥 Installation

```bash
# Clone the repository
git clone https://github.com/Raftys/DiabCalc.git

# Navigate to project directory
cd DiabCalc

# Build with Maven
mvn clean install

# Run the application
java -jar target/DiabCalc.jar
