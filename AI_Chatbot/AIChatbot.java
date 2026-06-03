import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

public class AIChatbot {

    static final Scanner scanner = new Scanner(System.in);
    static final Path logFile = Paths.get("chat_history.txt");
    static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    static final List<ChatMessage> history = new ArrayList<>();
    static final Map<String, String> memory = new HashMap<>();
    static final Map<String, String> knowledgeBase = new HashMap<>();
    static final List<String> jokes = new ArrayList<>();
    static final List<String> quotes = new ArrayList<>();
    static String lastTopic = "general";

    static {
        knowledgeBase.put("what is your name", "I am Nova, a conversational AI assistant.");
        knowledgeBase.put("who are you", "I am a virtual assistant designed to help you with questions and tasks.");
        knowledgeBase.put("what can you do", "I can answer questions, remember preferences, create summaries, and keep a warm conversation.");
        knowledgeBase.put("how do you work", "I analyze your text using language patterns and choose the best response based on intent.");

        jokes.add("Why do programmers prefer dark mode? Because light attracts bugs.");
        jokes.add("I would tell you a UDP joke, but you might not get it.");
        jokes.add("Why did the AI cross the road? To optimize the other side.");

        quotes.add("Code is like humor. When you have to explain it, it’s bad.");
        quotes.add("Simplicity is the soul of efficiency.");
        quotes.add("The best way to predict the future is to invent it.");
    }

    public static void main(String[] args) {
        loadHistory();
        printWelcome();
        while (true) {
            System.out.print("You: ");
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput stream closed. Exiting.");
                saveHistory();
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                continue;
            }
            addMessage("User", input);
            String response = generateResponse(input);
            addMessage("Nova", response);
            System.out.println("Nova: " + response);
            if (isExitCommand(input)) {
                saveHistory();
                break;
            }
        }
    }

    static void printWelcome() {
        System.out.println("AI Chatbot is ready. Type 'help' to begin.");
        if (!history.isEmpty()) {
            System.out.println("Previous session loaded. I remember our last discussion.");
            System.out.println("Type 'history' to review the recent conversation.");
        }
    }

    static String generateResponse(String input) {
        String normalized = input.toLowerCase(Locale.ENGLISH);
        if (normalized.matches(".*\\b(help|commands|options)\\b.*")) {
            return listCapabilities();
        }
        if (normalized.matches(".*\\b(history|transcript|log)\\b.*")) {
            return summarizeHistory();
        }
        if (normalized.matches(".*\\b(save|export)\\b.*")) {
            saveHistory();
            return "Conversation saved successfully.";
        }
        if (normalized.matches(".*\\b(clear|reset)\\b.*")) {
            history.clear();
            memory.clear();
            return "Conversation memory and history have been reset.";
        }
        if (normalized.matches(".*\\b(joke|funny|laugh)\\b.*")) {
            return randomItem(jokes);
        }
        if (normalized.matches(".*\\b(quote|inspire|motivate)\\b.*")) {
            return randomItem(quotes);
        }
        if (normalized.matches(".*\\b(time|clock)\\b.*")) {
            return "The current time is " + LocalTime.now().format(timeFormat) + ".";
        }
        if (normalized.matches(".*\\b(date|today)\\b.*")) {
            return "Today is " + LocalDate.now() + ".";
        }
        if (normalized.matches(".*\\b(weather|forecast)\\b.*")) {
            return "I cannot access live weather, but it looks like a good day to learn something new.";
        }
        if (normalized.matches(".*\\b(remember|remind)\\b.*")) {
            return handleMemory(normalized);
        }
        if (normalized.matches(".*\\b(what is my|who am i|my name)\\b.*")) {
            return recallMemory(normalized);
        }
        if (normalized.matches(".*\\b(search|find|lookup)\\b.*")) {
            return searchKnowledge(normalized);
        }
        if (knowledgeBase.containsKey(normalized)) {
            lastTopic = "knowledge";
            return knowledgeBase.get(normalized);
        }
        if (normalized.matches(".*\\b(hello|hi|hey|greetings)\\b.*")) {
            lastTopic = "greeting";
            return "Hello! I am Nova. How can I support you today?";
        }
        if (normalized.matches(".*\\b(thank you|thanks)\\b.*")) {
            return "You are welcome. If you need anything else, I am here.";
        }
        if (normalized.matches(".*\\b(bye|exit|quit|goodbye)\\b.*")) {
            return "Goodbye. I will be here when you need me again.";
        }
        if (normalized.matches(".*\\b(why|what|how|when|where)\\b.*")) {
            lastTopic = "question";
            return fallbackMessage(input);
        }
        return fallbackMessage(input);
    }

    static String handleMemory(String input) {
        Matcher setMatcher = Pattern.compile("remember that my (.+?) is (.+)", Pattern.CASE_INSENSITIVE).matcher(input);
        if (setMatcher.find()) {
            String key = setMatcher.group(1).strip();
            String value = setMatcher.group(2).strip();
            memory.put(key.toLowerCase(Locale.ENGLISH), value);
            return "I will remember that your " + key + " is " + value + ".";
        }
        Matcher noteMatcher = Pattern.compile("remember that (.+)", Pattern.CASE_INSENSITIVE).matcher(input);
        if (noteMatcher.find()) {
            String note = noteMatcher.group(1).strip();
            memory.put("note", note);
            return "I have noted: " + note + ".";
        }
        return "Tell me what you want me to remember in a single sentence.";
    }

    static String recallMemory(String input) {
        if (input.contains("name") || input.contains("who am i")) {
            return memory.getOrDefault("name", "I do not have your name saved yet.");
        }
        if (input.contains("note")) {
            return memory.getOrDefault("note", "I do not have any notes saved.");
        }
        return memory.values().stream().findFirst().map(value -> "I remember that: " + value + ".").orElse("I do not have any stored memory for that yet.");
    }

    static String searchKnowledge(String input) {
        for (String key : knowledgeBase.keySet()) {
            if (input.contains(key)) {
                return knowledgeBase.get(key);
            }
        }
        return "I could not find an exact answer, but I can still help with related questions.";
    }

    static String fallbackMessage(String input) {
        lastTopic = "fallback";
        if (memory.containsKey("note") && input.toLowerCase(Locale.ENGLISH).contains("note")) {
            return "You asked about your note. It says: " + memory.get("note") + ".";
        }
        return "I am still learning. Could you rephrase that or ask for help to see what I can do?";
    }

    static String listCapabilities() {
        return "I can chat, answer questions, remember details, tell jokes, generate quotes, summarize history, and save the conversation.";
    }

    static String summarizeHistory() {
        if (history.isEmpty()) {
            return "There is no conversation history yet.";
        }
        StringBuilder summary = new StringBuilder("Recent conversation summary:\n");
        int count = Math.min(history.size(), 6);
        for (int i = Math.max(0, history.size() - count); i < history.size(); i++) {
            ChatMessage message = history.get(i);
            summary.append(message.sender()).append(": ").append(message.text()).append("\n");
        }
        return summary.toString().trim();
    }

    static boolean isExitCommand(String input) {
        String normalized = input.toLowerCase(Locale.ENGLISH);
        return normalized.matches(".*\\b(bye|exit|quit|goodbye)\\b.*");
    }

    static void addMessage(String sender, String text) {
        history.add(new ChatMessage(sender, text, LocalDateTime.now()));
    }

    static void saveHistory() {
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (ChatMessage message : history) {
                writer.write(formatLog(message));
                writer.newLine();
            }
            history.clear();
        } catch (IOException e) {
            System.out.println("Unable to save chat history.");
        }
    }

    static void loadHistory() {
        if (!Files.exists(logFile)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", 3);
                if (parts.length == 3) {
                    history.add(new ChatMessage(parts[1], parts[2], LocalDateTime.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to load previous chat history.");
        }
    }

    static String formatLog(ChatMessage message) {
        return message.timestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\t" + message.sender() + "\t" + message.text();
    }

    static String randomItem(List<String> items) {
        return items.get(new Random().nextInt(items.size()));
    }

    static class ChatMessage {
        private final String sender;
        private final String text;
        private final LocalDateTime timestamp;

        ChatMessage(String sender, String text, LocalDateTime timestamp) {
            this.sender = sender;
            this.text = text;
            this.timestamp = timestamp;
        }

        String sender() {
            return sender;
        }

        String text() {
            return text;
        }

        LocalDateTime timestamp() {
            return timestamp;
        }
    }
}
