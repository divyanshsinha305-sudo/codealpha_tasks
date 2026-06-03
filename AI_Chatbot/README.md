AI Chatbot
===========

Overview
--------

This AI Chatbot is a single-file Java application designed to simulate a conversational assistant. It processes natural language input, maintains short-term memory, stores conversation history, and responds with contextual replies to common requests.

Features
--------

- Conversational interaction through a text-based interface
- Memory for user details and notes
- Conversation history persistence to `chat_history.txt`
- Built-in help, history, save, reset, and exit commands
- Intelligent responses for greetings, jokes, quotes, time, and date
- A fallback system for unknown questions and user guidance

Requirements
------------

- Java Development Kit (JDK) 17 or later
- A terminal or IDE with Java support

Usage
-----

1. Navigate to the project folder:

   ```bash
   cd codealpha_tasks/AI_Chatbot
   ```

2. Compile the program:

   ```bash
   javac AIChatbot.java
   ```

3. Run the chatbot:

   ```bash
   java AIChatbot
   ```

Project Structure
-----------------

- `AIChatbot.java`: main source file containing the chatbot logic
- `chat_history.txt`: saved conversation history generated at runtime
- `README.md`: project overview and instructions

System Workflow
---------------

- The chatbot loads any previous conversation history if available.
- Users type commands and questions directly into the console.
- Responses are generated from internal patterns, memory, and predefined knowledge.
- Conversations can be saved and reviewed, and the system retains memory during the session.

Notes
-----

Run the chatbot from the folder containing `AIChatbot.java`. The history file is created automatically when the chatbot saves a conversation.