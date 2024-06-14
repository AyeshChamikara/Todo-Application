# ToDo Application

## Overview

**Purpose:** The purpose of this todo application is to provide users with a convenient way to manage their tasks and todos on their mobile devices. The application leverages Firebase Authentication for secure user sign-up and sign-in, SQLite for storing and managing todo lists locally on the device, and SharedPreferences for remembering user login status and facilitating logout functionality.

## Key Features

1. **User Authentication with Firebase:**
   - Secure sign-up and sign-in using Firebase Authentication.
   - Supports email/password authentication method.

2. **Local Storage with SQLite:**
   - Store todo lists locally on the user's device using SQLite.
   - Access and manage todos even when offline.

3. **Todo List Management:**
   - **Add, Edit, Delete Todos:** Easily add new todos, edit existing ones, and delete completed or unnecessary todos.
   - **Prioritize Todos:** Prioritize todos by low, medium, or high importance.

4. **Login Remembering with SharedPreferences:**
   - Remember user login status using SharedPreferences.
   - Stay logged in even after closing the app, enhancing user convenience.
   - Securely log out and terminate the session.
