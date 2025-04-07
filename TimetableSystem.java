import java.util.*;

class TimetableSystem {
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final String[] timeSlots = {
            "09:00 - 09:50", "10:00 - 10:50", "11:00 - 11:50", "12:00 - 12:50 (Lunch Break)",
            "01:00 - 02:40 (Lab Slot)", "02:50 - 03:40"
    };
    private final String[] reservedLabs = {"A1-312", "A2-201", "A2-202", "A1-208"};

    private List<String> availableClassrooms = Arrays.asList(
            "Room-101", "Room-102", "Room-103", "Room-104", "Room-105",
            "Room-106", "Room-107", "Room-108", "Room-109", "Room-110"
    );

    private Map<String, List<String>> subjects = new HashMap<>();
    private Map<String, String> facultyAssignments = new HashMap<>();
    private Set<String> labSubjects = new HashSet<>();
    private Map<String, Integer> labFrequency = new HashMap<>();
    private Map<String, String[]> timetableCE = new HashMap<>();
    private Map<String, String[]> timetableIT = new HashMap<>();
    private Set<String> occupiedClassrooms = new HashSet<>();

    public void getUserInput() {
        Scanner sc = new Scanner(System.in);

        // CE Department Input
        System.out.println("Enter the number of subjects for B.Tech CE:");
        int ceCount = sc.nextInt();
        sc.nextLine();

        List<String> ceSubjects = new ArrayList<>();
        for (int i = 0; i < ceCount; i++) {
            System.out.println("Enter subject " + (i + 1) + " for CE:");
            String subject = sc.nextLine();
            ceSubjects.add(subject);

            String labStatus;
            while (true) {
                System.out.println("Does " + subject + " have a lab? (yes/no):");
                labStatus = sc.nextLine().trim().toLowerCase();
                if (labStatus.equals("yes") || labStatus.equals("no")) break;
                System.out.println("Invalid input. Please enter 'yes' or 'no' only.");
            }

            if (labStatus.equals("yes")) {
                labSubjects.add(subject);
                System.out.println("How many lab sessions per week for " + subject + "?");
                int freq = sc.nextInt();
                sc.nextLine();
                labFrequency.put(subject, freq);
            }

            System.out.println("Enter faculty name for " + subject + ":");
            facultyAssignments.put(subject, sc.nextLine());
        }
        subjects.put("B.Tech CE", ceSubjects);

        // IT Department Input
        System.out.println("Enter the number of subjects for B.Tech IT:");
        int itCount = sc.nextInt();
        sc.nextLine();

        List<String> itSubjects = new ArrayList<>();
        for (int i = 0; i < itCount; i++) {
            System.out.println("Enter subject " + (i + 1) + " for IT:");
            String subject = sc.nextLine();
            itSubjects.add(subject);

            String labStatus;
            while (true) {
                System.out.println("Does " + subject + " have a lab? (yes/no):");
                labStatus = sc.nextLine().trim().toLowerCase();
                if (labStatus.equals("yes") || labStatus.equals("no")) break;
                System.out.println("Invalid input. Please enter 'yes' or 'no' only.");
            }

            if (labStatus.equals("yes")) {
                labSubjects.add(subject);
                System.out.println("How many lab sessions per week for " + subject + "?");
                int freq = sc.nextInt();
                sc.nextLine();
                labFrequency.put(subject, freq);
            }

            System.out.println("Enter faculty name for " + subject + ":");
            facultyAssignments.put(subject, sc.nextLine());
        }
        subjects.put("B.Tech IT", itSubjects);
    }

    public void generateTimetable() {
        Map<String, Integer> ceLabAssigned = new HashMap<>();
        Map<String, Integer> itLabAssigned = new HashMap<>();

        for (String day : days) {
            timetableCE.put(day, new String[timeSlots.length]);
            timetableIT.put(day, new String[timeSlots.length]);

            List<String> ceSubjects = new ArrayList<>(subjects.get("B.Tech CE"));
            List<String> itSubjects = new ArrayList<>(subjects.get("B.Tech IT"));

            Collections.shuffle(ceSubjects);
            Collections.shuffle(itSubjects);

            for (int i = 0; i < timeSlots.length; i++) {
                if (i == 3) {
                    timetableCE.get(day)[i] = "Lunch Break";
                    timetableIT.get(day)[i] = "Lunch Break";
                } else if (i == 4) {
                    timetableCE.get(day)[i] = formatCell(assignLab(ceSubjects, ceLabAssigned));
                    timetableIT.get(day)[i] = formatCell(assignLab(itSubjects, itLabAssigned));
                } else {
                    timetableCE.get(day)[i] = formatCell(assignTheoryRoom(ceSubjects));
                    timetableIT.get(day)[i] = formatCell(assignTheoryRoom(itSubjects));
                }
            }
        }
    }

    private String assignLab(List<String> subjectsList, Map<String, Integer> assignedCount) {
        for (String subject : subjectsList) {
            if (labSubjects.contains(subject)) {
                int assigned = assignedCount.getOrDefault(subject, 0);
                int max = labFrequency.getOrDefault(subject, 0);
                if (assigned < max) {
                    assignedCount.put(subject, assigned + 1);
                    String room = reservedLabs[new Random().nextInt(reservedLabs.length)];
                    return subject + " - " + facultyAssignments.get(subject) + " @ " + room;
                }
            }
        }
        return "No Lab Assigned";
    }

    private String assignTheoryRoom(List<String> subjectsList) {
        Iterator<String> iterator = subjectsList.iterator();
        while (iterator.hasNext()) {
            String subject = iterator.next();
            if (!labSubjects.contains(subject)) {
                iterator.remove();
                for (String room : availableClassrooms) {
                    if (!occupiedClassrooms.contains(room)) {
                        occupiedClassrooms.add(room);
                        return subject + " - " + facultyAssignments.get(subject) + " @ " + room;
                    }
                }
            }
        }
        return "TBD";
    }

    private String formatCell(String text) {
        return String.format("%-40s", text);
    }

    public void displayTimetable() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                  WEEKLY TIMETABLE                                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════════╝");

        for (String day : days) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                       " + day + "                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════════╝");
            System.out.printf("╔%-22s╦%-40s╦%-40s╗\n", "══════════════════════", "════════════════════════════════════════", "════════════════════════════════════════");
            System.out.printf("║ %-20s ║ %-38s ║ %-38s ║\n", "Time Slot", "B.Tech CE", "B.Tech IT");
            System.out.printf("╠%-22s╬%-40s╬%-40s╣\n", "══════════════════════", "════════════════════════════════════════", "════════════════════════════════════════");

            for (int j = 0; j < timeSlots.length; j++) {
                System.out.printf("║ %-20s ║ %-38s ║ %-38s ║\n", timeSlots[j], timetableCE.get(day)[j], timetableIT.get(day)[j]);
            }
            System.out.printf("╚%-22s╩%-40s╩%-40s╝\n", "══════════════════════", "════════════════════════════════════════", "════════════════════════════════════════");
        }
    }

    public static void main(String[] args) {
        TimetableSystem ts = new TimetableSystem();
        ts.getUserInput();
        ts.generateTimetable();
        ts.displayTimetable();
    }
}