

# Expense Tracker

____________________________________________________________________________

## Project Title
Expense Tracker – Java Swing Desktop Application

____________________________________________________________________________

## Overview of the Project
The Expense Tracker is a simple, offline desktop application designed to help users 
record, categorize, and analyze their income and expenses. It uses Java Swing for the 
graphical interface and stores data in CSV format for transparency and ease of access.  
The application also features a visual expense report generated through a custom pie chart.

____________________________________________________________________________

## Features
- Add income and expense entries  
- Categorize transactions  
- View all records in a tabular form  
- Automatic calculation of:
  - Total Income  
  - Total Expense  
  - Net Balance  
- Save and load data using CSV files  
- Generate a visual pie-chart expense report  
- Lightweight, standalone, and offline-friendly  

____________________________________________________________________________

## Technologies / Tools Used
- Java 8 or above  
- Java Swing (UI components)  
- Java AWT & Graphics2D (chart rendering)  
- Java I/O (FileReader, FileWriter, BufferedReader, BufferedWriter)  
- JTable, JDialog, JFrame, JPanel  

____________________________________________________________________________

## Steps to Install & Run the Project

### 1. Clone the repository
```

https://github.com/Piyush-Aggarwal-github/DigitalNotepad.git
```

### 2. Compile the source files
```

javac -d out src/*.java

```

### 3. Run the project
```

java -cp out Main

```

The application window will open, and you can begin recording transactions.

____________________________________________________________________________

## Instructions for Testing the Project

Use the following steps to test the main functionality:

### **1. Add Transactions**
- Click the **Add** button  
- Enter Type (INCOME/EXPENSE), Category, Description, Amount, Date  
- Submit and check if the entry appears in the table

### **2. Remove Transactions**
- Select a row in the table  
- Click **Remove**  
- Ensure the record is deleted

### **3. Save to CSV**
- Add some entries  
- Click **Save**  
- Confirm that a CSV file is generated with correct formatting

### **4. Load from CSV**
- Click **Load**  
- Select an existing CSV file  
- Verify that the table populates correctly

### **5. Generate Report**
- Click **Report**  
- Confirm the pie chart displays correct category-wise expense distribution

### **6. Invalid Data Testing**
- Enter alphabets in the amount field  
- Enter invalid dates  
- Load corrupted/malformed CSV  
- Ensure the application handles errors gracefully

____________________________________________________________________________

