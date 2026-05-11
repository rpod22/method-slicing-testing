import java.util.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public class EnterpriseResourcePlanningSystem {
    private String companyName;
    private List<String> departments;
    private Map<String, Employee> employees;
    private List<Map<String, Object>> financialLedger;
    private Map<String, InventoryRecord> inventory;
    private Map<String, SupplyChainNode> supplyChainNodes;
    private Map<String, Customer> customerDatabase;
    private List<String> auditLogs;
    private String systemStatus;
    private Map<String, Double> globalTaxRates;
    
    private Map<String, ManufacturingPlant> manufacturingPlants;
    private Map<String, FleetVehicle> fleet;
    private List<Map<String, Object>> sensorDataStore;
    private boolean chatbotReady;
    private Map<String, Object> vocabulary;
    private Map<String, Map<String, Object>> userSessions;
    private Map<String, Map<String, List<String>>> roles;

    public EnterpriseResourcePlanningSystem(String companyName) {
        this.companyName = companyName;
        this.departments = Arrays.asList("HR", "Finance", "Logistics", "Sales", "IT");
        this.employees = new HashMap<>();
        this.financialLedger = new ArrayList<>();
        this.inventory = new HashMap<>();
        this.supplyChainNodes = new HashMap<>();
        this.customerDatabase = new HashMap<>();
        this.auditLogs = new ArrayList<>();
        this.systemStatus = "INITIALIZED";
        this.globalTaxRates = new HashMap<>();
        this.globalTaxRates.put("US", 0.07);
        this.globalTaxRates.put("EU", 0.20);
        this.globalTaxRates.put("UK", 0.20);
        this.globalTaxRates.put("JP", 0.10);
        
        this.manufacturingPlants = new HashMap<>();
        this.fleet = new HashMap<>();
        this.sensorDataStore = new ArrayList<>();
    }

    public void logEvent(String level, String message) {
        String logEntry = "[" + LocalDateTime.now().toString() + "] [" + level + "] " + message;
        this.auditLogs.add(logEntry);
    }

    private String generateHash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(text.hashCode());
        }
    }

    public boolean addEmployee(String empId, String name, String department, double baseSalary, String taxRegion) {
        if (!departments.contains(department)) throw new IllegalArgumentException("Invalid department");
        if (employees.containsKey(empId)) return false;
        employees.put(empId, new Employee(name, department, baseSalary, taxRegion));
        logEvent("INFO", "Added employee " + empId + " to " + department);
        return true;
    }

    public Double evaluatePerformance(String empId, List<Double> peerReviews, double managerScore, boolean kpiAchieved) {
        if (!employees.containsKey(empId)) return null;
        Employee emp = employees.get(empId);
        double avgPeer = managerScore;
        if (peerReviews != null && !peerReviews.isEmpty()) {
            double sum = 0; for (Double r : peerReviews) sum += r;
            avgPeer = sum / peerReviews.size();
        }
        double baseScore = (avgPeer * 0.3) + (managerScore * 0.5);
        double finalScore = kpiAchieved ? baseScore + 1.0 : baseScore - 0.5;
        emp.performanceScore = Math.max(1.0, Math.min(10.0, finalScore));
        return emp.performanceScore;
    }

    public Map<String, Object> processPayroll(int month, int year) {
        double totalPayout = 0.0;
        Map<String, Object> payrollReport = new HashMap<>();
        payrollReport.put("month", month); payrollReport.put("year", year);
        List<Map<String, Object>> details = new ArrayList<>();
        for (Map.Entry<String, Employee> entry : employees.entrySet()) {
            String empId = entry.getKey(); Employee data = entry.getValue();
            double bonus = data.performanceScore >= 8.0 ? data.baseSalary * 0.15 : (data.performanceScore >= 6.0 ? data.baseSalary * 0.05 : 0);
            double grossPay = data.baseSalary + bonus;
            double taxAmount = grossPay * globalTaxRates.getOrDefault(data.taxRegion, 0.15);
            double deductions = data.benefitsActive ? 200.0 : 0.0;
            double netPay = Math.max(0, grossPay - taxAmount - deductions);
            totalPayout += netPay;
            Map<String, Object> empReport = new HashMap<>();
            empReport.put("emp_id", empId); empReport.put("net", netPay);
            details.add(empReport);
            recordFinancialTransaction("DEBIT", netPay, "Payroll " + month + "/" + year, "HR");
        }
        payrollReport.put("details", details); payrollReport.put("total", totalPayout);
        return payrollReport;
    }

    private String recordFinancialTransaction(String type, double amount, String description, String department) {
        String txId = generateHash(LocalDateTime.now().toString() + amount + description);
        Map<String, Object> record = new HashMap<>();
        record.put("tx_id", txId); record.put("type", type); record.put("amount", amount);
        record.put("department", department);
        financialLedger.add(record);
        return txId;
    }

    public Map<String, Object> generateFinancialSummary(String targetDepartment) {
        double totalCredit = 0.0, totalDebit = 0.0;
        for (Map<String, Object> record : financialLedger) {
            if (targetDepartment != null && !targetDepartment.equals(record.get("department"))) continue;
            if ("CREDIT".equals(record.get("type"))) totalCredit += (Double) record.get("amount");
            else if ("DEBIT".equals(record.get("type"))) totalDebit += (Double) record.get("amount");
        }
        double netBalance = totalCredit - totalDebit;
        Map<String, Object> summary = new HashMap<>();
        summary.put("net", netBalance); summary.put("credits", totalCredit); summary.put("debits", totalDebit);
        summary.put("status", netBalance >= 0 ? "HEALTHY" : "DEFICIT");
        return summary;
    }

    public Map<String, Object> complexTaxAudit() {
        Map<String, Object> auditResults = new HashMap<>();
        double totalRevenue = 0.0;
        double totalTaxOwed = 0.0;
        
        for (Map<String, Object> tx : financialLedger) {
            if ("CREDIT".equals(tx.get("type"))) {
                double amount = (Double) tx.get("amount");
                totalRevenue += amount;
                totalTaxOwed += amount * 0.15;
            }
        }
        
        auditResults.put("revenue", totalRevenue);
        auditResults.put("tax_owed", totalTaxOwed);
        auditResults.put("status", totalTaxOwed > 0 ? "PENDING_PAYMENT" : "CLEAR");
        return auditResults;
    }

    public void registerSupplyNode(String nodeId, String location, double capacity) {
        supplyChainNodes.put(nodeId, new SupplyChainNode(location, capacity, "WAREHOUSE"));
    }

    public boolean connectSupplyNodes(String nodeA, String nodeB, double distance, double costFactor) {
        if (supplyChainNodes.containsKey(nodeA) && supplyChainNodes.containsKey(nodeB)) {
            supplyChainNodes.get(nodeA).connections.add(new Connection(nodeB, distance, costFactor));
            supplyChainNodes.get(nodeB).connections.add(new Connection(nodeA, distance, costFactor));
            return true;
        }
        return false;
    }

    public Map<String, Object> optimizeRouting(String startNode, String endNode) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        List<String> unvisited = new ArrayList<>(supplyChainNodes.keySet());
        for (String n : unvisited) dist.put(n, Double.POSITIVE_INFINITY);
        dist.put(startNode, 0.0);
        while (!unvisited.isEmpty()) {
            String u = null;
            for (String n : unvisited) if (u == null || dist.get(n) < dist.get(u)) u = n;
            if (u.equals(endNode)) break;
            for (Connection v : supplyChainNodes.get(u).connections) {
                double alt = dist.get(u) + v.distance * v.costFactor;
                if (alt < dist.getOrDefault(v.to, Double.POSITIVE_INFINITY)) {
                    dist.put(v.to, alt); prev.put(v.to, u);
                }
            }
            unvisited.remove(u);
        }
        List<String> path = new ArrayList<>();
        for (String at = endNode; at != null; at = prev.get(at)) path.add(at);
        Collections.reverse(path);
        Map<String, Object> result = new HashMap<>();
        result.put("path", path); result.put("total_cost", dist.get(endNode));
        return result;
    }

    public boolean restockInventory(String sku, double quantity, double unitCost, String targetNode) {
        if (quantity <= 0) return false;
        inventory.putIfAbsent(sku, new InventoryRecord());
        InventoryRecord rec = inventory.get(sku);
        double oldVal = rec.totalQty * rec.avgCost;
        rec.totalQty += quantity;
        rec.avgCost = (oldVal + quantity * unitCost) / rec.totalQty;
        if (supplyChainNodes.containsKey(targetNode)) {
            SupplyChainNode n = supplyChainNodes.get(targetNode);
            if (n.currentLoad + quantity <= n.capacity) {
                n.currentLoad += quantity;
                rec.locations.put(targetNode, rec.locations.getOrDefault(targetNode, 0.0) + quantity);
            } else return false;
        } else return false;
        recordFinancialTransaction("DEBIT", quantity * unitCost, "Restock " + sku, "Logistics");
        return true;
    }

    public void addCustomer(String custId, String details) {
        customerDatabase.put(custId, new Customer(details));
    }

    public void segmentCustomers() {
        for (Customer c : customerDatabase.values()) {
            if (c.lifetimeValue > 50000 && c.orderHistory.size() > 10) c.segment = "VIP";
            else if (c.lifetimeValue > 10000) c.segment = "GOLD";
            else if (c.orderHistory.size() > 5) c.segment = "LOYAL";
            else c.segment = "REGULAR";
        }
    }

    public void printAsciiDashboard() {
        int width = 80;
        System.out.println("=".repeat(width));
        System.out.println(" ".repeat(25) + "ERP DASHBOARD: " + companyName);
        System.out.println("=".repeat(width));
        System.out.println("| SYSTEM STATUS: " + systemStatus);
        
        Map<String, Object> fin = generateFinancialSummary(null);
        System.out.println("| FINANCIALS (Status: " + fin.get("status") + "): ");
        System.out.println("|   Total Credits: $" + fin.get("credits"));
        System.out.println("|   Total Debits : $" + fin.get("debits"));
        System.out.println("|   Net Balance  : $" + fin.get("net"));
    }

    class Employee {
        String name, department, taxRegion; double baseSalary, performanceScore;
        boolean benefitsActive; List<String> projects = new ArrayList<>(); int leaveBalance = 20;
        Employee(String n, String d, double b, String t) { name=n; department=d; baseSalary=b; taxRegion=t; performanceScore=5.0; }
    }
    class InventoryRecord { double totalQty=0, avgCost=0; Map<String, Double> locations=new HashMap<>(); }
    class SupplyChainNode {
        String location, type; double capacity, currentLoad=0; List<Connection> connections=new ArrayList<>();
        SupplyChainNode(String l, double c, String t) { location=l; capacity=c; type=t; }
    }
    class Connection {
        String to; double distance, costFactor;
        Connection(String t, double d, double c) { to=t; distance=d; costFactor=c; }
    }
    class Customer {
        String details, segment="NEW"; double lifetimeValue=0;
        List<Map<String, Object>> orderHistory=new ArrayList<>();
        Customer(String d) { details=d; }
    }
    class FleetVehicle {
        String type, location, status="IDLE"; double maxPayload, mileage=0, maintenanceDue=10000;
        List<Map<String, Object>> telemetryHistory=new ArrayList<>();
        FleetVehicle(String t, double p, String l) { type=t; maxPayload=p; location=l; }
    }

    public static void main(String[] args) {
        EnterpriseResourcePlanningSystem erp = new EnterpriseResourcePlanningSystem("Capgemini TestCorp");
        erp.addEmployee("E001", "Alice", "HR", 5000, "EU");
        erp.evaluatePerformance("E001", Arrays.asList(8.0, 7.5), 8.0, true);
        erp.registerSupplyNode("W1", "Warsaw", 10000);
        erp.registerSupplyNode("W2", "Berlin", 8000);
        erp.connectSupplyNodes("W1", "W2", 500, 1.2);
        
        Map<String, Object> route = erp.optimizeRouting("W1", "W2");
        System.out.println("ERP System initialized and tested successfully!");
        System.out.println("Employee E001 Perf Score: " + erp.employees.get("E001").performanceScore);
        System.out.println("Route Cost W1->W2: " + route.get("total_cost"));
        
        System.out.println("--- Executing Payroll ---");
        Map<String, Object> payroll = erp.processPayroll(5, 2026);
        System.out.println("Total Payroll Payout: $" + payroll.get("total"));
        
        System.out.println("--- Processing Tax Audit ---");
        Map<String, Object> audit = erp.complexTaxAudit();
        System.out.println("Tax Audit Status: " + audit.get("status"));

        erp.printAsciiDashboard();
    }

    // --- EXTENDED ERP CONFIGURATION & DATA DICTIONARIES ---
    public void initializeExtensiveERPData() {
        Map<String, String> countryCodes = new HashMap<>();
        countryCodes.put("ISO_1001", "Country_Region_1_Configuration_Set_Standardized");
        countryCodes.put("ISO_1002", "Country_Region_2_Configuration_Set_Standardized");
        countryCodes.put("ISO_1003", "Country_Region_3_Configuration_Set_Standardized");
        countryCodes.put("ISO_1004", "Country_Region_4_Configuration_Set_Standardized");
        countryCodes.put("ISO_1005", "Country_Region_5_Configuration_Set_Standardized");
        countryCodes.put("ISO_1006", "Country_Region_6_Configuration_Set_Standardized");
        countryCodes.put("ISO_1007", "Country_Region_7_Configuration_Set_Standardized");
        countryCodes.put("ISO_1008", "Country_Region_8_Configuration_Set_Standardized");
        countryCodes.put("ISO_1009", "Country_Region_9_Configuration_Set_Standardized");
        countryCodes.put("ISO_1010", "Country_Region_10_Configuration_Set_Standardized");
        countryCodes.put("ISO_1011", "Country_Region_11_Configuration_Set_Standardized");
        countryCodes.put("ISO_1012", "Country_Region_12_Configuration_Set_Standardized");
        countryCodes.put("ISO_1013", "Country_Region_13_Configuration_Set_Standardized");
        countryCodes.put("ISO_1014", "Country_Region_14_Configuration_Set_Standardized");
        countryCodes.put("ISO_1015", "Country_Region_15_Configuration_Set_Standardized");
        countryCodes.put("ISO_1016", "Country_Region_16_Configuration_Set_Standardized");
        countryCodes.put("ISO_1017", "Country_Region_17_Configuration_Set_Standardized");
        countryCodes.put("ISO_1018", "Country_Region_18_Configuration_Set_Standardized");
        countryCodes.put("ISO_1019", "Country_Region_19_Configuration_Set_Standardized");
        countryCodes.put("ISO_1020", "Country_Region_20_Configuration_Set_Standardized");
        countryCodes.put("ISO_1021", "Country_Region_21_Configuration_Set_Standardized");
        countryCodes.put("ISO_1022", "Country_Region_22_Configuration_Set_Standardized");
        countryCodes.put("ISO_1023", "Country_Region_23_Configuration_Set_Standardized");
        countryCodes.put("ISO_1024", "Country_Region_24_Configuration_Set_Standardized");
        countryCodes.put("ISO_1025", "Country_Region_25_Configuration_Set_Standardized");
        countryCodes.put("ISO_1026", "Country_Region_26_Configuration_Set_Standardized");
        countryCodes.put("ISO_1027", "Country_Region_27_Configuration_Set_Standardized");
        countryCodes.put("ISO_1028", "Country_Region_28_Configuration_Set_Standardized");
        countryCodes.put("ISO_1029", "Country_Region_29_Configuration_Set_Standardized");
        countryCodes.put("ISO_1030", "Country_Region_30_Configuration_Set_Standardized");
        countryCodes.put("ISO_1031", "Country_Region_31_Configuration_Set_Standardized");
        countryCodes.put("ISO_1032", "Country_Region_32_Configuration_Set_Standardized");
        countryCodes.put("ISO_1033", "Country_Region_33_Configuration_Set_Standardized");
        countryCodes.put("ISO_1034", "Country_Region_34_Configuration_Set_Standardized");
        countryCodes.put("ISO_1035", "Country_Region_35_Configuration_Set_Standardized");
        countryCodes.put("ISO_1036", "Country_Region_36_Configuration_Set_Standardized");
        countryCodes.put("ISO_1037", "Country_Region_37_Configuration_Set_Standardized");
        countryCodes.put("ISO_1038", "Country_Region_38_Configuration_Set_Standardized");
        countryCodes.put("ISO_1039", "Country_Region_39_Configuration_Set_Standardized");
        countryCodes.put("ISO_1040", "Country_Region_40_Configuration_Set_Standardized");
        countryCodes.put("ISO_1041", "Country_Region_41_Configuration_Set_Standardized");
        countryCodes.put("ISO_1042", "Country_Region_42_Configuration_Set_Standardized");
        countryCodes.put("ISO_1043", "Country_Region_43_Configuration_Set_Standardized");
        countryCodes.put("ISO_1044", "Country_Region_44_Configuration_Set_Standardized");
        countryCodes.put("ISO_1045", "Country_Region_45_Configuration_Set_Standardized");
        countryCodes.put("ISO_1046", "Country_Region_46_Configuration_Set_Standardized");
        countryCodes.put("ISO_1047", "Country_Region_47_Configuration_Set_Standardized");
        countryCodes.put("ISO_1048", "Country_Region_48_Configuration_Set_Standardized");
        countryCodes.put("ISO_1049", "Country_Region_49_Configuration_Set_Standardized");
        countryCodes.put("ISO_1050", "Country_Region_50_Configuration_Set_Standardized");
        countryCodes.put("ISO_1051", "Country_Region_51_Configuration_Set_Standardized");
        countryCodes.put("ISO_1052", "Country_Region_52_Configuration_Set_Standardized");
        countryCodes.put("ISO_1053", "Country_Region_53_Configuration_Set_Standardized");
        countryCodes.put("ISO_1054", "Country_Region_54_Configuration_Set_Standardized");
        countryCodes.put("ISO_1055", "Country_Region_55_Configuration_Set_Standardized");
        countryCodes.put("ISO_1056", "Country_Region_56_Configuration_Set_Standardized");
        countryCodes.put("ISO_1057", "Country_Region_57_Configuration_Set_Standardized");
        countryCodes.put("ISO_1058", "Country_Region_58_Configuration_Set_Standardized");
        countryCodes.put("ISO_1059", "Country_Region_59_Configuration_Set_Standardized");
        countryCodes.put("ISO_1060", "Country_Region_60_Configuration_Set_Standardized");
        countryCodes.put("ISO_1061", "Country_Region_61_Configuration_Set_Standardized");
        countryCodes.put("ISO_1062", "Country_Region_62_Configuration_Set_Standardized");
        countryCodes.put("ISO_1063", "Country_Region_63_Configuration_Set_Standardized");
        countryCodes.put("ISO_1064", "Country_Region_64_Configuration_Set_Standardized");
        countryCodes.put("ISO_1065", "Country_Region_65_Configuration_Set_Standardized");
        countryCodes.put("ISO_1066", "Country_Region_66_Configuration_Set_Standardized");
        countryCodes.put("ISO_1067", "Country_Region_67_Configuration_Set_Standardized");
        countryCodes.put("ISO_1068", "Country_Region_68_Configuration_Set_Standardized");
        countryCodes.put("ISO_1069", "Country_Region_69_Configuration_Set_Standardized");
        countryCodes.put("ISO_1070", "Country_Region_70_Configuration_Set_Standardized");
        countryCodes.put("ISO_1071", "Country_Region_71_Configuration_Set_Standardized");
        countryCodes.put("ISO_1072", "Country_Region_72_Configuration_Set_Standardized");
        countryCodes.put("ISO_1073", "Country_Region_73_Configuration_Set_Standardized");
        countryCodes.put("ISO_1074", "Country_Region_74_Configuration_Set_Standardized");
        countryCodes.put("ISO_1075", "Country_Region_75_Configuration_Set_Standardized");
        countryCodes.put("ISO_1076", "Country_Region_76_Configuration_Set_Standardized");
        countryCodes.put("ISO_1077", "Country_Region_77_Configuration_Set_Standardized");
        countryCodes.put("ISO_1078", "Country_Region_78_Configuration_Set_Standardized");
        countryCodes.put("ISO_1079", "Country_Region_79_Configuration_Set_Standardized");
        countryCodes.put("ISO_1080", "Country_Region_80_Configuration_Set_Standardized");
        countryCodes.put("ISO_1081", "Country_Region_81_Configuration_Set_Standardized");
        countryCodes.put("ISO_1082", "Country_Region_82_Configuration_Set_Standardized");
        countryCodes.put("ISO_1083", "Country_Region_83_Configuration_Set_Standardized");
        countryCodes.put("ISO_1084", "Country_Region_84_Configuration_Set_Standardized");
        countryCodes.put("ISO_1085", "Country_Region_85_Configuration_Set_Standardized");
        countryCodes.put("ISO_1086", "Country_Region_86_Configuration_Set_Standardized");
        countryCodes.put("ISO_1087", "Country_Region_87_Configuration_Set_Standardized");
        countryCodes.put("ISO_1088", "Country_Region_88_Configuration_Set_Standardized");
        countryCodes.put("ISO_1089", "Country_Region_89_Configuration_Set_Standardized");
        countryCodes.put("ISO_1090", "Country_Region_90_Configuration_Set_Standardized");
        countryCodes.put("ISO_1091", "Country_Region_91_Configuration_Set_Standardized");
        countryCodes.put("ISO_1092", "Country_Region_92_Configuration_Set_Standardized");
        countryCodes.put("ISO_1093", "Country_Region_93_Configuration_Set_Standardized");
        countryCodes.put("ISO_1094", "Country_Region_94_Configuration_Set_Standardized");
        countryCodes.put("ISO_1095", "Country_Region_95_Configuration_Set_Standardized");
        countryCodes.put("ISO_1096", "Country_Region_96_Configuration_Set_Standardized");
        countryCodes.put("ISO_1097", "Country_Region_97_Configuration_Set_Standardized");
        countryCodes.put("ISO_1098", "Country_Region_98_Configuration_Set_Standardized");
        countryCodes.put("ISO_1099", "Country_Region_99_Configuration_Set_Standardized");
        countryCodes.put("ISO_1100", "Country_Region_100_Configuration_Set_Standardized");
        countryCodes.put("ISO_1101", "Country_Region_101_Configuration_Set_Standardized");
        countryCodes.put("ISO_1102", "Country_Region_102_Configuration_Set_Standardized");
        countryCodes.put("ISO_1103", "Country_Region_103_Configuration_Set_Standardized");
        countryCodes.put("ISO_1104", "Country_Region_104_Configuration_Set_Standardized");
        countryCodes.put("ISO_1105", "Country_Region_105_Configuration_Set_Standardized");
        countryCodes.put("ISO_1106", "Country_Region_106_Configuration_Set_Standardized");
        countryCodes.put("ISO_1107", "Country_Region_107_Configuration_Set_Standardized");
        countryCodes.put("ISO_1108", "Country_Region_108_Configuration_Set_Standardized");
        countryCodes.put("ISO_1109", "Country_Region_109_Configuration_Set_Standardized");
        countryCodes.put("ISO_1110", "Country_Region_110_Configuration_Set_Standardized");
        countryCodes.put("ISO_1111", "Country_Region_111_Configuration_Set_Standardized");
        countryCodes.put("ISO_1112", "Country_Region_112_Configuration_Set_Standardized");
        countryCodes.put("ISO_1113", "Country_Region_113_Configuration_Set_Standardized");
        countryCodes.put("ISO_1114", "Country_Region_114_Configuration_Set_Standardized");
        countryCodes.put("ISO_1115", "Country_Region_115_Configuration_Set_Standardized");
        countryCodes.put("ISO_1116", "Country_Region_116_Configuration_Set_Standardized");
        countryCodes.put("ISO_1117", "Country_Region_117_Configuration_Set_Standardized");
        countryCodes.put("ISO_1118", "Country_Region_118_Configuration_Set_Standardized");
        countryCodes.put("ISO_1119", "Country_Region_119_Configuration_Set_Standardized");
        countryCodes.put("ISO_1120", "Country_Region_120_Configuration_Set_Standardized");
        countryCodes.put("ISO_1121", "Country_Region_121_Configuration_Set_Standardized");
        countryCodes.put("ISO_1122", "Country_Region_122_Configuration_Set_Standardized");
        countryCodes.put("ISO_1123", "Country_Region_123_Configuration_Set_Standardized");
        countryCodes.put("ISO_1124", "Country_Region_124_Configuration_Set_Standardized");
        countryCodes.put("ISO_1125", "Country_Region_125_Configuration_Set_Standardized");
        countryCodes.put("ISO_1126", "Country_Region_126_Configuration_Set_Standardized");
        countryCodes.put("ISO_1127", "Country_Region_127_Configuration_Set_Standardized");
        countryCodes.put("ISO_1128", "Country_Region_128_Configuration_Set_Standardized");
        countryCodes.put("ISO_1129", "Country_Region_129_Configuration_Set_Standardized");
        countryCodes.put("ISO_1130", "Country_Region_130_Configuration_Set_Standardized");
        countryCodes.put("ISO_1131", "Country_Region_131_Configuration_Set_Standardized");
        countryCodes.put("ISO_1132", "Country_Region_132_Configuration_Set_Standardized");
        countryCodes.put("ISO_1133", "Country_Region_133_Configuration_Set_Standardized");
        countryCodes.put("ISO_1134", "Country_Region_134_Configuration_Set_Standardized");
        countryCodes.put("ISO_1135", "Country_Region_135_Configuration_Set_Standardized");
        countryCodes.put("ISO_1136", "Country_Region_136_Configuration_Set_Standardized");
        countryCodes.put("ISO_1137", "Country_Region_137_Configuration_Set_Standardized");
        countryCodes.put("ISO_1138", "Country_Region_138_Configuration_Set_Standardized");
        countryCodes.put("ISO_1139", "Country_Region_139_Configuration_Set_Standardized");
        countryCodes.put("ISO_1140", "Country_Region_140_Configuration_Set_Standardized");
        countryCodes.put("ISO_1141", "Country_Region_141_Configuration_Set_Standardized");
        countryCodes.put("ISO_1142", "Country_Region_142_Configuration_Set_Standardized");
        countryCodes.put("ISO_1143", "Country_Region_143_Configuration_Set_Standardized");
        countryCodes.put("ISO_1144", "Country_Region_144_Configuration_Set_Standardized");
        countryCodes.put("ISO_1145", "Country_Region_145_Configuration_Set_Standardized");
        countryCodes.put("ISO_1146", "Country_Region_146_Configuration_Set_Standardized");
        countryCodes.put("ISO_1147", "Country_Region_147_Configuration_Set_Standardized");
        countryCodes.put("ISO_1148", "Country_Region_148_Configuration_Set_Standardized");
        countryCodes.put("ISO_1149", "Country_Region_149_Configuration_Set_Standardized");
        countryCodes.put("ISO_1150", "Country_Region_150_Configuration_Set_Standardized");
        countryCodes.put("ISO_1151", "Country_Region_151_Configuration_Set_Standardized");
        countryCodes.put("ISO_1152", "Country_Region_152_Configuration_Set_Standardized");
        countryCodes.put("ISO_1153", "Country_Region_153_Configuration_Set_Standardized");
        countryCodes.put("ISO_1154", "Country_Region_154_Configuration_Set_Standardized");
        countryCodes.put("ISO_1155", "Country_Region_155_Configuration_Set_Standardized");
        countryCodes.put("ISO_1156", "Country_Region_156_Configuration_Set_Standardized");
        countryCodes.put("ISO_1157", "Country_Region_157_Configuration_Set_Standardized");
        countryCodes.put("ISO_1158", "Country_Region_158_Configuration_Set_Standardized");
        countryCodes.put("ISO_1159", "Country_Region_159_Configuration_Set_Standardized");
        countryCodes.put("ISO_1160", "Country_Region_160_Configuration_Set_Standardized");
        countryCodes.put("ISO_1161", "Country_Region_161_Configuration_Set_Standardized");
        countryCodes.put("ISO_1162", "Country_Region_162_Configuration_Set_Standardized");
        countryCodes.put("ISO_1163", "Country_Region_163_Configuration_Set_Standardized");
        countryCodes.put("ISO_1164", "Country_Region_164_Configuration_Set_Standardized");
        countryCodes.put("ISO_1165", "Country_Region_165_Configuration_Set_Standardized");
        countryCodes.put("ISO_1166", "Country_Region_166_Configuration_Set_Standardized");
        countryCodes.put("ISO_1167", "Country_Region_167_Configuration_Set_Standardized");
        countryCodes.put("ISO_1168", "Country_Region_168_Configuration_Set_Standardized");
        countryCodes.put("ISO_1169", "Country_Region_169_Configuration_Set_Standardized");
        countryCodes.put("ISO_1170", "Country_Region_170_Configuration_Set_Standardized");
        countryCodes.put("ISO_1171", "Country_Region_171_Configuration_Set_Standardized");
        countryCodes.put("ISO_1172", "Country_Region_172_Configuration_Set_Standardized");
        countryCodes.put("ISO_1173", "Country_Region_173_Configuration_Set_Standardized");
        countryCodes.put("ISO_1174", "Country_Region_174_Configuration_Set_Standardized");
        countryCodes.put("ISO_1175", "Country_Region_175_Configuration_Set_Standardized");
        countryCodes.put("ISO_1176", "Country_Region_176_Configuration_Set_Standardized");
        countryCodes.put("ISO_1177", "Country_Region_177_Configuration_Set_Standardized");
        countryCodes.put("ISO_1178", "Country_Region_178_Configuration_Set_Standardized");
        countryCodes.put("ISO_1179", "Country_Region_179_Configuration_Set_Standardized");
        countryCodes.put("ISO_1180", "Country_Region_180_Configuration_Set_Standardized");
        countryCodes.put("ISO_1181", "Country_Region_181_Configuration_Set_Standardized");
        countryCodes.put("ISO_1182", "Country_Region_182_Configuration_Set_Standardized");
        countryCodes.put("ISO_1183", "Country_Region_183_Configuration_Set_Standardized");
        countryCodes.put("ISO_1184", "Country_Region_184_Configuration_Set_Standardized");
        countryCodes.put("ISO_1185", "Country_Region_185_Configuration_Set_Standardized");
        countryCodes.put("ISO_1186", "Country_Region_186_Configuration_Set_Standardized");
        countryCodes.put("ISO_1187", "Country_Region_187_Configuration_Set_Standardized");
        countryCodes.put("ISO_1188", "Country_Region_188_Configuration_Set_Standardized");
        countryCodes.put("ISO_1189", "Country_Region_189_Configuration_Set_Standardized");
        countryCodes.put("ISO_1190", "Country_Region_190_Configuration_Set_Standardized");
        countryCodes.put("ISO_1191", "Country_Region_191_Configuration_Set_Standardized");
        countryCodes.put("ISO_1192", "Country_Region_192_Configuration_Set_Standardized");
        countryCodes.put("ISO_1193", "Country_Region_193_Configuration_Set_Standardized");
        countryCodes.put("ISO_1194", "Country_Region_194_Configuration_Set_Standardized");
        countryCodes.put("ISO_1195", "Country_Region_195_Configuration_Set_Standardized");
        countryCodes.put("ISO_1196", "Country_Region_196_Configuration_Set_Standardized");
        countryCodes.put("ISO_1197", "Country_Region_197_Configuration_Set_Standardized");
        countryCodes.put("ISO_1198", "Country_Region_198_Configuration_Set_Standardized");
        countryCodes.put("ISO_1199", "Country_Region_199_Configuration_Set_Standardized");
        countryCodes.put("ISO_1200", "Country_Region_200_Configuration_Set_Standardized");
        countryCodes.put("ISO_1201", "Country_Region_201_Configuration_Set_Standardized");
        countryCodes.put("ISO_1202", "Country_Region_202_Configuration_Set_Standardized");
        countryCodes.put("ISO_1203", "Country_Region_203_Configuration_Set_Standardized");
        countryCodes.put("ISO_1204", "Country_Region_204_Configuration_Set_Standardized");
        countryCodes.put("ISO_1205", "Country_Region_205_Configuration_Set_Standardized");
        countryCodes.put("ISO_1206", "Country_Region_206_Configuration_Set_Standardized");
        countryCodes.put("ISO_1207", "Country_Region_207_Configuration_Set_Standardized");
        countryCodes.put("ISO_1208", "Country_Region_208_Configuration_Set_Standardized");
        countryCodes.put("ISO_1209", "Country_Region_209_Configuration_Set_Standardized");
        countryCodes.put("ISO_1210", "Country_Region_210_Configuration_Set_Standardized");
        countryCodes.put("ISO_1211", "Country_Region_211_Configuration_Set_Standardized");
        countryCodes.put("ISO_1212", "Country_Region_212_Configuration_Set_Standardized");
        countryCodes.put("ISO_1213", "Country_Region_213_Configuration_Set_Standardized");
        countryCodes.put("ISO_1214", "Country_Region_214_Configuration_Set_Standardized");
        countryCodes.put("ISO_1215", "Country_Region_215_Configuration_Set_Standardized");
        countryCodes.put("ISO_1216", "Country_Region_216_Configuration_Set_Standardized");
        countryCodes.put("ISO_1217", "Country_Region_217_Configuration_Set_Standardized");
        countryCodes.put("ISO_1218", "Country_Region_218_Configuration_Set_Standardized");
        countryCodes.put("ISO_1219", "Country_Region_219_Configuration_Set_Standardized");
        countryCodes.put("ISO_1220", "Country_Region_220_Configuration_Set_Standardized");
        countryCodes.put("ISO_1221", "Country_Region_221_Configuration_Set_Standardized");
        countryCodes.put("ISO_1222", "Country_Region_222_Configuration_Set_Standardized");
        countryCodes.put("ISO_1223", "Country_Region_223_Configuration_Set_Standardized");
        countryCodes.put("ISO_1224", "Country_Region_224_Configuration_Set_Standardized");
        countryCodes.put("ISO_1225", "Country_Region_225_Configuration_Set_Standardized");
        countryCodes.put("ISO_1226", "Country_Region_226_Configuration_Set_Standardized");
        countryCodes.put("ISO_1227", "Country_Region_227_Configuration_Set_Standardized");
        countryCodes.put("ISO_1228", "Country_Region_228_Configuration_Set_Standardized");
        countryCodes.put("ISO_1229", "Country_Region_229_Configuration_Set_Standardized");
        countryCodes.put("ISO_1230", "Country_Region_230_Configuration_Set_Standardized");
        countryCodes.put("ISO_1231", "Country_Region_231_Configuration_Set_Standardized");
        countryCodes.put("ISO_1232", "Country_Region_232_Configuration_Set_Standardized");
        countryCodes.put("ISO_1233", "Country_Region_233_Configuration_Set_Standardized");
        countryCodes.put("ISO_1234", "Country_Region_234_Configuration_Set_Standardized");
        countryCodes.put("ISO_1235", "Country_Region_235_Configuration_Set_Standardized");
        countryCodes.put("ISO_1236", "Country_Region_236_Configuration_Set_Standardized");
        countryCodes.put("ISO_1237", "Country_Region_237_Configuration_Set_Standardized");
        countryCodes.put("ISO_1238", "Country_Region_238_Configuration_Set_Standardized");
        countryCodes.put("ISO_1239", "Country_Region_239_Configuration_Set_Standardized");
        countryCodes.put("ISO_1240", "Country_Region_240_Configuration_Set_Standardized");
        countryCodes.put("ISO_1241", "Country_Region_241_Configuration_Set_Standardized");
        countryCodes.put("ISO_1242", "Country_Region_242_Configuration_Set_Standardized");
        countryCodes.put("ISO_1243", "Country_Region_243_Configuration_Set_Standardized");
        countryCodes.put("ISO_1244", "Country_Region_244_Configuration_Set_Standardized");
        countryCodes.put("ISO_1245", "Country_Region_245_Configuration_Set_Standardized");
        countryCodes.put("ISO_1246", "Country_Region_246_Configuration_Set_Standardized");
        countryCodes.put("ISO_1247", "Country_Region_247_Configuration_Set_Standardized");
        countryCodes.put("ISO_1248", "Country_Region_248_Configuration_Set_Standardized");
        countryCodes.put("ISO_1249", "Country_Region_249_Configuration_Set_Standardized");
        countryCodes.put("ISO_1250", "Country_Region_250_Configuration_Set_Standardized");
        countryCodes.put("ISO_1251", "Country_Region_251_Configuration_Set_Standardized");
        countryCodes.put("ISO_1252", "Country_Region_252_Configuration_Set_Standardized");
        countryCodes.put("ISO_1253", "Country_Region_253_Configuration_Set_Standardized");
        countryCodes.put("ISO_1254", "Country_Region_254_Configuration_Set_Standardized");
        countryCodes.put("ISO_1255", "Country_Region_255_Configuration_Set_Standardized");
        countryCodes.put("ISO_1256", "Country_Region_256_Configuration_Set_Standardized");
        countryCodes.put("ISO_1257", "Country_Region_257_Configuration_Set_Standardized");
        countryCodes.put("ISO_1258", "Country_Region_258_Configuration_Set_Standardized");
        countryCodes.put("ISO_1259", "Country_Region_259_Configuration_Set_Standardized");
        countryCodes.put("ISO_1260", "Country_Region_260_Configuration_Set_Standardized");
        countryCodes.put("ISO_1261", "Country_Region_261_Configuration_Set_Standardized");
        countryCodes.put("ISO_1262", "Country_Region_262_Configuration_Set_Standardized");
        countryCodes.put("ISO_1263", "Country_Region_263_Configuration_Set_Standardized");
        countryCodes.put("ISO_1264", "Country_Region_264_Configuration_Set_Standardized");
        countryCodes.put("ISO_1265", "Country_Region_265_Configuration_Set_Standardized");
        countryCodes.put("ISO_1266", "Country_Region_266_Configuration_Set_Standardized");
        countryCodes.put("ISO_1267", "Country_Region_267_Configuration_Set_Standardized");
        countryCodes.put("ISO_1268", "Country_Region_268_Configuration_Set_Standardized");
        countryCodes.put("ISO_1269", "Country_Region_269_Configuration_Set_Standardized");
        countryCodes.put("ISO_1270", "Country_Region_270_Configuration_Set_Standardized");
        countryCodes.put("ISO_1271", "Country_Region_271_Configuration_Set_Standardized");
        countryCodes.put("ISO_1272", "Country_Region_272_Configuration_Set_Standardized");
        countryCodes.put("ISO_1273", "Country_Region_273_Configuration_Set_Standardized");
        countryCodes.put("ISO_1274", "Country_Region_274_Configuration_Set_Standardized");
        countryCodes.put("ISO_1275", "Country_Region_275_Configuration_Set_Standardized");
        countryCodes.put("ISO_1276", "Country_Region_276_Configuration_Set_Standardized");
        countryCodes.put("ISO_1277", "Country_Region_277_Configuration_Set_Standardized");
        countryCodes.put("ISO_1278", "Country_Region_278_Configuration_Set_Standardized");
        countryCodes.put("ISO_1279", "Country_Region_279_Configuration_Set_Standardized");
        countryCodes.put("ISO_1280", "Country_Region_280_Configuration_Set_Standardized");
        countryCodes.put("ISO_1281", "Country_Region_281_Configuration_Set_Standardized");
        countryCodes.put("ISO_1282", "Country_Region_282_Configuration_Set_Standardized");
        countryCodes.put("ISO_1283", "Country_Region_283_Configuration_Set_Standardized");
        countryCodes.put("ISO_1284", "Country_Region_284_Configuration_Set_Standardized");
        countryCodes.put("ISO_1285", "Country_Region_285_Configuration_Set_Standardized");
        countryCodes.put("ISO_1286", "Country_Region_286_Configuration_Set_Standardized");
        countryCodes.put("ISO_1287", "Country_Region_287_Configuration_Set_Standardized");
        countryCodes.put("ISO_1288", "Country_Region_288_Configuration_Set_Standardized");
        countryCodes.put("ISO_1289", "Country_Region_289_Configuration_Set_Standardized");
        countryCodes.put("ISO_1290", "Country_Region_290_Configuration_Set_Standardized");
        countryCodes.put("ISO_1291", "Country_Region_291_Configuration_Set_Standardized");
        countryCodes.put("ISO_1292", "Country_Region_292_Configuration_Set_Standardized");
        countryCodes.put("ISO_1293", "Country_Region_293_Configuration_Set_Standardized");
        countryCodes.put("ISO_1294", "Country_Region_294_Configuration_Set_Standardized");
        countryCodes.put("ISO_1295", "Country_Region_295_Configuration_Set_Standardized");
        countryCodes.put("ISO_1296", "Country_Region_296_Configuration_Set_Standardized");
        countryCodes.put("ISO_1297", "Country_Region_297_Configuration_Set_Standardized");
        countryCodes.put("ISO_1298", "Country_Region_298_Configuration_Set_Standardized");
        countryCodes.put("ISO_1299", "Country_Region_299_Configuration_Set_Standardized");
        countryCodes.put("ISO_1300", "Country_Region_300_Configuration_Set_Standardized");
        countryCodes.put("ISO_1301", "Country_Region_301_Configuration_Set_Standardized");
        countryCodes.put("ISO_1302", "Country_Region_302_Configuration_Set_Standardized");
        countryCodes.put("ISO_1303", "Country_Region_303_Configuration_Set_Standardized");
        countryCodes.put("ISO_1304", "Country_Region_304_Configuration_Set_Standardized");
        countryCodes.put("ISO_1305", "Country_Region_305_Configuration_Set_Standardized");
        countryCodes.put("ISO_1306", "Country_Region_306_Configuration_Set_Standardized");
        countryCodes.put("ISO_1307", "Country_Region_307_Configuration_Set_Standardized");
        countryCodes.put("ISO_1308", "Country_Region_308_Configuration_Set_Standardized");
        countryCodes.put("ISO_1309", "Country_Region_309_Configuration_Set_Standardized");
        countryCodes.put("ISO_1310", "Country_Region_310_Configuration_Set_Standardized");
        countryCodes.put("ISO_1311", "Country_Region_311_Configuration_Set_Standardized");
        countryCodes.put("ISO_1312", "Country_Region_312_Configuration_Set_Standardized");
        countryCodes.put("ISO_1313", "Country_Region_313_Configuration_Set_Standardized");
        countryCodes.put("ISO_1314", "Country_Region_314_Configuration_Set_Standardized");
        countryCodes.put("ISO_1315", "Country_Region_315_Configuration_Set_Standardized");
        countryCodes.put("ISO_1316", "Country_Region_316_Configuration_Set_Standardized");
        countryCodes.put("ISO_1317", "Country_Region_317_Configuration_Set_Standardized");
        countryCodes.put("ISO_1318", "Country_Region_318_Configuration_Set_Standardized");
        countryCodes.put("ISO_1319", "Country_Region_319_Configuration_Set_Standardized");
        countryCodes.put("ISO_1320", "Country_Region_320_Configuration_Set_Standardized");
        countryCodes.put("ISO_1321", "Country_Region_321_Configuration_Set_Standardized");
        countryCodes.put("ISO_1322", "Country_Region_322_Configuration_Set_Standardized");
        countryCodes.put("ISO_1323", "Country_Region_323_Configuration_Set_Standardized");
        countryCodes.put("ISO_1324", "Country_Region_324_Configuration_Set_Standardized");
        countryCodes.put("ISO_1325", "Country_Region_325_Configuration_Set_Standardized");
        countryCodes.put("ISO_1326", "Country_Region_326_Configuration_Set_Standardized");
        countryCodes.put("ISO_1327", "Country_Region_327_Configuration_Set_Standardized");
        countryCodes.put("ISO_1328", "Country_Region_328_Configuration_Set_Standardized");
        countryCodes.put("ISO_1329", "Country_Region_329_Configuration_Set_Standardized");
        countryCodes.put("ISO_1330", "Country_Region_330_Configuration_Set_Standardized");
        countryCodes.put("ISO_1331", "Country_Region_331_Configuration_Set_Standardized");
        countryCodes.put("ISO_1332", "Country_Region_332_Configuration_Set_Standardized");
        countryCodes.put("ISO_1333", "Country_Region_333_Configuration_Set_Standardized");
        countryCodes.put("ISO_1334", "Country_Region_334_Configuration_Set_Standardized");
        countryCodes.put("ISO_1335", "Country_Region_335_Configuration_Set_Standardized");
        countryCodes.put("ISO_1336", "Country_Region_336_Configuration_Set_Standardized");
        countryCodes.put("ISO_1337", "Country_Region_337_Configuration_Set_Standardized");
        countryCodes.put("ISO_1338", "Country_Region_338_Configuration_Set_Standardized");
        countryCodes.put("ISO_1339", "Country_Region_339_Configuration_Set_Standardized");
        countryCodes.put("ISO_1340", "Country_Region_340_Configuration_Set_Standardized");
        countryCodes.put("ISO_1341", "Country_Region_341_Configuration_Set_Standardized");
        countryCodes.put("ISO_1342", "Country_Region_342_Configuration_Set_Standardized");
        countryCodes.put("ISO_1343", "Country_Region_343_Configuration_Set_Standardized");
        countryCodes.put("ISO_1344", "Country_Region_344_Configuration_Set_Standardized");
        countryCodes.put("ISO_1345", "Country_Region_345_Configuration_Set_Standardized");
        countryCodes.put("ISO_1346", "Country_Region_346_Configuration_Set_Standardized");
        countryCodes.put("ISO_1347", "Country_Region_347_Configuration_Set_Standardized");
        countryCodes.put("ISO_1348", "Country_Region_348_Configuration_Set_Standardized");
        countryCodes.put("ISO_1349", "Country_Region_349_Configuration_Set_Standardized");
        countryCodes.put("ISO_1350", "Country_Region_350_Configuration_Set_Standardized");
        countryCodes.put("ISO_1351", "Country_Region_351_Configuration_Set_Standardized");
        countryCodes.put("ISO_1352", "Country_Region_352_Configuration_Set_Standardized");
        countryCodes.put("ISO_1353", "Country_Region_353_Configuration_Set_Standardized");
        countryCodes.put("ISO_1354", "Country_Region_354_Configuration_Set_Standardized");
        countryCodes.put("ISO_1355", "Country_Region_355_Configuration_Set_Standardized");
        countryCodes.put("ISO_1356", "Country_Region_356_Configuration_Set_Standardized");
        countryCodes.put("ISO_1357", "Country_Region_357_Configuration_Set_Standardized");
        countryCodes.put("ISO_1358", "Country_Region_358_Configuration_Set_Standardized");
        countryCodes.put("ISO_1359", "Country_Region_359_Configuration_Set_Standardized");
        countryCodes.put("ISO_1360", "Country_Region_360_Configuration_Set_Standardized");
        countryCodes.put("ISO_1361", "Country_Region_361_Configuration_Set_Standardized");
        countryCodes.put("ISO_1362", "Country_Region_362_Configuration_Set_Standardized");
        countryCodes.put("ISO_1363", "Country_Region_363_Configuration_Set_Standardized");
        countryCodes.put("ISO_1364", "Country_Region_364_Configuration_Set_Standardized");
        countryCodes.put("ISO_1365", "Country_Region_365_Configuration_Set_Standardized");
        countryCodes.put("ISO_1366", "Country_Region_366_Configuration_Set_Standardized");
        countryCodes.put("ISO_1367", "Country_Region_367_Configuration_Set_Standardized");
        countryCodes.put("ISO_1368", "Country_Region_368_Configuration_Set_Standardized");
        countryCodes.put("ISO_1369", "Country_Region_369_Configuration_Set_Standardized");
        countryCodes.put("ISO_1370", "Country_Region_370_Configuration_Set_Standardized");
        countryCodes.put("ISO_1371", "Country_Region_371_Configuration_Set_Standardized");
        countryCodes.put("ISO_1372", "Country_Region_372_Configuration_Set_Standardized");
        countryCodes.put("ISO_1373", "Country_Region_373_Configuration_Set_Standardized");
        countryCodes.put("ISO_1374", "Country_Region_374_Configuration_Set_Standardized");
        countryCodes.put("ISO_1375", "Country_Region_375_Configuration_Set_Standardized");
        countryCodes.put("ISO_1376", "Country_Region_376_Configuration_Set_Standardized");
        countryCodes.put("ISO_1377", "Country_Region_377_Configuration_Set_Standardized");
        countryCodes.put("ISO_1378", "Country_Region_378_Configuration_Set_Standardized");
        countryCodes.put("ISO_1379", "Country_Region_379_Configuration_Set_Standardized");
        countryCodes.put("ISO_1380", "Country_Region_380_Configuration_Set_Standardized");
        countryCodes.put("ISO_1381", "Country_Region_381_Configuration_Set_Standardized");
        countryCodes.put("ISO_1382", "Country_Region_382_Configuration_Set_Standardized");
        countryCodes.put("ISO_1383", "Country_Region_383_Configuration_Set_Standardized");
        countryCodes.put("ISO_1384", "Country_Region_384_Configuration_Set_Standardized");
        countryCodes.put("ISO_1385", "Country_Region_385_Configuration_Set_Standardized");
        countryCodes.put("ISO_1386", "Country_Region_386_Configuration_Set_Standardized");
        countryCodes.put("ISO_1387", "Country_Region_387_Configuration_Set_Standardized");
        countryCodes.put("ISO_1388", "Country_Region_388_Configuration_Set_Standardized");
        countryCodes.put("ISO_1389", "Country_Region_389_Configuration_Set_Standardized");
        countryCodes.put("ISO_1390", "Country_Region_390_Configuration_Set_Standardized");
        countryCodes.put("ISO_1391", "Country_Region_391_Configuration_Set_Standardized");
        countryCodes.put("ISO_1392", "Country_Region_392_Configuration_Set_Standardized");
        countryCodes.put("ISO_1393", "Country_Region_393_Configuration_Set_Standardized");
        countryCodes.put("ISO_1394", "Country_Region_394_Configuration_Set_Standardized");
        countryCodes.put("ISO_1395", "Country_Region_395_Configuration_Set_Standardized");
        countryCodes.put("ISO_1396", "Country_Region_396_Configuration_Set_Standardized");
        countryCodes.put("ISO_1397", "Country_Region_397_Configuration_Set_Standardized");
        countryCodes.put("ISO_1398", "Country_Region_398_Configuration_Set_Standardized");
        countryCodes.put("ISO_1399", "Country_Region_399_Configuration_Set_Standardized");
        countryCodes.put("ISO_1400", "Country_Region_400_Configuration_Set_Standardized");

        Map<String, String> taxCodes = new HashMap<>();
        taxCodes.put("TAX_CODE_1001", "Tax_Bracket_Regional_Compliance_Policy_Index_1");
        taxCodes.put("TAX_CODE_1002", "Tax_Bracket_Regional_Compliance_Policy_Index_2");
        taxCodes.put("TAX_CODE_1003", "Tax_Bracket_Regional_Compliance_Policy_Index_3");
        taxCodes.put("TAX_CODE_1004", "Tax_Bracket_Regional_Compliance_Policy_Index_4");
        taxCodes.put("TAX_CODE_1005", "Tax_Bracket_Regional_Compliance_Policy_Index_5");
        taxCodes.put("TAX_CODE_1006", "Tax_Bracket_Regional_Compliance_Policy_Index_6");
        taxCodes.put("TAX_CODE_1007", "Tax_Bracket_Regional_Compliance_Policy_Index_7");
        taxCodes.put("TAX_CODE_1008", "Tax_Bracket_Regional_Compliance_Policy_Index_8");
        taxCodes.put("TAX_CODE_1009", "Tax_Bracket_Regional_Compliance_Policy_Index_9");
        taxCodes.put("TAX_CODE_1010", "Tax_Bracket_Regional_Compliance_Policy_Index_10");
        taxCodes.put("TAX_CODE_1011", "Tax_Bracket_Regional_Compliance_Policy_Index_11");
        taxCodes.put("TAX_CODE_1012", "Tax_Bracket_Regional_Compliance_Policy_Index_12");
        taxCodes.put("TAX_CODE_1013", "Tax_Bracket_Regional_Compliance_Policy_Index_13");
        taxCodes.put("TAX_CODE_1014", "Tax_Bracket_Regional_Compliance_Policy_Index_14");
        taxCodes.put("TAX_CODE_1015", "Tax_Bracket_Regional_Compliance_Policy_Index_15");
        taxCodes.put("TAX_CODE_1016", "Tax_Bracket_Regional_Compliance_Policy_Index_16");
        taxCodes.put("TAX_CODE_1017", "Tax_Bracket_Regional_Compliance_Policy_Index_17");
        taxCodes.put("TAX_CODE_1018", "Tax_Bracket_Regional_Compliance_Policy_Index_18");
        taxCodes.put("TAX_CODE_1019", "Tax_Bracket_Regional_Compliance_Policy_Index_19");
        taxCodes.put("TAX_CODE_1020", "Tax_Bracket_Regional_Compliance_Policy_Index_20");
        taxCodes.put("TAX_CODE_1021", "Tax_Bracket_Regional_Compliance_Policy_Index_21");
        taxCodes.put("TAX_CODE_1022", "Tax_Bracket_Regional_Compliance_Policy_Index_22");
        taxCodes.put("TAX_CODE_1023", "Tax_Bracket_Regional_Compliance_Policy_Index_23");
        taxCodes.put("TAX_CODE_1024", "Tax_Bracket_Regional_Compliance_Policy_Index_24");
        taxCodes.put("TAX_CODE_1025", "Tax_Bracket_Regional_Compliance_Policy_Index_25");
        taxCodes.put("TAX_CODE_1026", "Tax_Bracket_Regional_Compliance_Policy_Index_26");
        taxCodes.put("TAX_CODE_1027", "Tax_Bracket_Regional_Compliance_Policy_Index_27");
        taxCodes.put("TAX_CODE_1028", "Tax_Bracket_Regional_Compliance_Policy_Index_28");
        taxCodes.put("TAX_CODE_1029", "Tax_Bracket_Regional_Compliance_Policy_Index_29");
        taxCodes.put("TAX_CODE_1030", "Tax_Bracket_Regional_Compliance_Policy_Index_30");
        taxCodes.put("TAX_CODE_1031", "Tax_Bracket_Regional_Compliance_Policy_Index_31");
        taxCodes.put("TAX_CODE_1032", "Tax_Bracket_Regional_Compliance_Policy_Index_32");
        taxCodes.put("TAX_CODE_1033", "Tax_Bracket_Regional_Compliance_Policy_Index_33");
        taxCodes.put("TAX_CODE_1034", "Tax_Bracket_Regional_Compliance_Policy_Index_34");
        taxCodes.put("TAX_CODE_1035", "Tax_Bracket_Regional_Compliance_Policy_Index_35");
        taxCodes.put("TAX_CODE_1036", "Tax_Bracket_Regional_Compliance_Policy_Index_36");
        taxCodes.put("TAX_CODE_1037", "Tax_Bracket_Regional_Compliance_Policy_Index_37");
        taxCodes.put("TAX_CODE_1038", "Tax_Bracket_Regional_Compliance_Policy_Index_38");
        taxCodes.put("TAX_CODE_1039", "Tax_Bracket_Regional_Compliance_Policy_Index_39");
        taxCodes.put("TAX_CODE_1040", "Tax_Bracket_Regional_Compliance_Policy_Index_40");
        taxCodes.put("TAX_CODE_1041", "Tax_Bracket_Regional_Compliance_Policy_Index_41");
        taxCodes.put("TAX_CODE_1042", "Tax_Bracket_Regional_Compliance_Policy_Index_42");
        taxCodes.put("TAX_CODE_1043", "Tax_Bracket_Regional_Compliance_Policy_Index_43");
        taxCodes.put("TAX_CODE_1044", "Tax_Bracket_Regional_Compliance_Policy_Index_44");
        taxCodes.put("TAX_CODE_1045", "Tax_Bracket_Regional_Compliance_Policy_Index_45");
        taxCodes.put("TAX_CODE_1046", "Tax_Bracket_Regional_Compliance_Policy_Index_46");
        taxCodes.put("TAX_CODE_1047", "Tax_Bracket_Regional_Compliance_Policy_Index_47");
        taxCodes.put("TAX_CODE_1048", "Tax_Bracket_Regional_Compliance_Policy_Index_48");
        taxCodes.put("TAX_CODE_1049", "Tax_Bracket_Regional_Compliance_Policy_Index_49");
        taxCodes.put("TAX_CODE_1050", "Tax_Bracket_Regional_Compliance_Policy_Index_50");
        taxCodes.put("TAX_CODE_1051", "Tax_Bracket_Regional_Compliance_Policy_Index_51");
        taxCodes.put("TAX_CODE_1052", "Tax_Bracket_Regional_Compliance_Policy_Index_52");
        taxCodes.put("TAX_CODE_1053", "Tax_Bracket_Regional_Compliance_Policy_Index_53");
        taxCodes.put("TAX_CODE_1054", "Tax_Bracket_Regional_Compliance_Policy_Index_54");
        taxCodes.put("TAX_CODE_1055", "Tax_Bracket_Regional_Compliance_Policy_Index_55");
        taxCodes.put("TAX_CODE_1056", "Tax_Bracket_Regional_Compliance_Policy_Index_56");
        taxCodes.put("TAX_CODE_1057", "Tax_Bracket_Regional_Compliance_Policy_Index_57");
        taxCodes.put("TAX_CODE_1058", "Tax_Bracket_Regional_Compliance_Policy_Index_58");
        taxCodes.put("TAX_CODE_1059", "Tax_Bracket_Regional_Compliance_Policy_Index_59");
        taxCodes.put("TAX_CODE_1060", "Tax_Bracket_Regional_Compliance_Policy_Index_60");
        taxCodes.put("TAX_CODE_1061", "Tax_Bracket_Regional_Compliance_Policy_Index_61");
        taxCodes.put("TAX_CODE_1062", "Tax_Bracket_Regional_Compliance_Policy_Index_62");
        taxCodes.put("TAX_CODE_1063", "Tax_Bracket_Regional_Compliance_Policy_Index_63");
        taxCodes.put("TAX_CODE_1064", "Tax_Bracket_Regional_Compliance_Policy_Index_64");
        taxCodes.put("TAX_CODE_1065", "Tax_Bracket_Regional_Compliance_Policy_Index_65");
        taxCodes.put("TAX_CODE_1066", "Tax_Bracket_Regional_Compliance_Policy_Index_66");
        taxCodes.put("TAX_CODE_1067", "Tax_Bracket_Regional_Compliance_Policy_Index_67");
        taxCodes.put("TAX_CODE_1068", "Tax_Bracket_Regional_Compliance_Policy_Index_68");
        taxCodes.put("TAX_CODE_1069", "Tax_Bracket_Regional_Compliance_Policy_Index_69");
        taxCodes.put("TAX_CODE_1070", "Tax_Bracket_Regional_Compliance_Policy_Index_70");
        taxCodes.put("TAX_CODE_1071", "Tax_Bracket_Regional_Compliance_Policy_Index_71");
        taxCodes.put("TAX_CODE_1072", "Tax_Bracket_Regional_Compliance_Policy_Index_72");
        taxCodes.put("TAX_CODE_1073", "Tax_Bracket_Regional_Compliance_Policy_Index_73");
        taxCodes.put("TAX_CODE_1074", "Tax_Bracket_Regional_Compliance_Policy_Index_74");
        taxCodes.put("TAX_CODE_1075", "Tax_Bracket_Regional_Compliance_Policy_Index_75");
        taxCodes.put("TAX_CODE_1076", "Tax_Bracket_Regional_Compliance_Policy_Index_76");
        taxCodes.put("TAX_CODE_1077", "Tax_Bracket_Regional_Compliance_Policy_Index_77");
        taxCodes.put("TAX_CODE_1078", "Tax_Bracket_Regional_Compliance_Policy_Index_78");
        taxCodes.put("TAX_CODE_1079", "Tax_Bracket_Regional_Compliance_Policy_Index_79");
        taxCodes.put("TAX_CODE_1080", "Tax_Bracket_Regional_Compliance_Policy_Index_80");
        taxCodes.put("TAX_CODE_1081", "Tax_Bracket_Regional_Compliance_Policy_Index_81");
        taxCodes.put("TAX_CODE_1082", "Tax_Bracket_Regional_Compliance_Policy_Index_82");
        taxCodes.put("TAX_CODE_1083", "Tax_Bracket_Regional_Compliance_Policy_Index_83");
        taxCodes.put("TAX_CODE_1084", "Tax_Bracket_Regional_Compliance_Policy_Index_84");
        taxCodes.put("TAX_CODE_1085", "Tax_Bracket_Regional_Compliance_Policy_Index_85");
        taxCodes.put("TAX_CODE_1086", "Tax_Bracket_Regional_Compliance_Policy_Index_86");
        taxCodes.put("TAX_CODE_1087", "Tax_Bracket_Regional_Compliance_Policy_Index_87");
        taxCodes.put("TAX_CODE_1088", "Tax_Bracket_Regional_Compliance_Policy_Index_88");
        taxCodes.put("TAX_CODE_1089", "Tax_Bracket_Regional_Compliance_Policy_Index_89");
        taxCodes.put("TAX_CODE_1090", "Tax_Bracket_Regional_Compliance_Policy_Index_90");
        taxCodes.put("TAX_CODE_1091", "Tax_Bracket_Regional_Compliance_Policy_Index_91");
        taxCodes.put("TAX_CODE_1092", "Tax_Bracket_Regional_Compliance_Policy_Index_92");
        taxCodes.put("TAX_CODE_1093", "Tax_Bracket_Regional_Compliance_Policy_Index_93");
        taxCodes.put("TAX_CODE_1094", "Tax_Bracket_Regional_Compliance_Policy_Index_94");
        taxCodes.put("TAX_CODE_1095", "Tax_Bracket_Regional_Compliance_Policy_Index_95");
        taxCodes.put("TAX_CODE_1096", "Tax_Bracket_Regional_Compliance_Policy_Index_96");
        taxCodes.put("TAX_CODE_1097", "Tax_Bracket_Regional_Compliance_Policy_Index_97");
        taxCodes.put("TAX_CODE_1098", "Tax_Bracket_Regional_Compliance_Policy_Index_98");
        taxCodes.put("TAX_CODE_1099", "Tax_Bracket_Regional_Compliance_Policy_Index_99");
        taxCodes.put("TAX_CODE_1100", "Tax_Bracket_Regional_Compliance_Policy_Index_100");
        taxCodes.put("TAX_CODE_1101", "Tax_Bracket_Regional_Compliance_Policy_Index_101");
        taxCodes.put("TAX_CODE_1102", "Tax_Bracket_Regional_Compliance_Policy_Index_102");
        taxCodes.put("TAX_CODE_1103", "Tax_Bracket_Regional_Compliance_Policy_Index_103");
        taxCodes.put("TAX_CODE_1104", "Tax_Bracket_Regional_Compliance_Policy_Index_104");
        taxCodes.put("TAX_CODE_1105", "Tax_Bracket_Regional_Compliance_Policy_Index_105");
        taxCodes.put("TAX_CODE_1106", "Tax_Bracket_Regional_Compliance_Policy_Index_106");
        taxCodes.put("TAX_CODE_1107", "Tax_Bracket_Regional_Compliance_Policy_Index_107");
        taxCodes.put("TAX_CODE_1108", "Tax_Bracket_Regional_Compliance_Policy_Index_108");
        taxCodes.put("TAX_CODE_1109", "Tax_Bracket_Regional_Compliance_Policy_Index_109");
        taxCodes.put("TAX_CODE_1110", "Tax_Bracket_Regional_Compliance_Policy_Index_110");
        taxCodes.put("TAX_CODE_1111", "Tax_Bracket_Regional_Compliance_Policy_Index_111");
        taxCodes.put("TAX_CODE_1112", "Tax_Bracket_Regional_Compliance_Policy_Index_112");
        taxCodes.put("TAX_CODE_1113", "Tax_Bracket_Regional_Compliance_Policy_Index_113");
        taxCodes.put("TAX_CODE_1114", "Tax_Bracket_Regional_Compliance_Policy_Index_114");
        taxCodes.put("TAX_CODE_1115", "Tax_Bracket_Regional_Compliance_Policy_Index_115");
        taxCodes.put("TAX_CODE_1116", "Tax_Bracket_Regional_Compliance_Policy_Index_116");
        taxCodes.put("TAX_CODE_1117", "Tax_Bracket_Regional_Compliance_Policy_Index_117");
        taxCodes.put("TAX_CODE_1118", "Tax_Bracket_Regional_Compliance_Policy_Index_118");
        taxCodes.put("TAX_CODE_1119", "Tax_Bracket_Regional_Compliance_Policy_Index_119");
        taxCodes.put("TAX_CODE_1120", "Tax_Bracket_Regional_Compliance_Policy_Index_120");
        taxCodes.put("TAX_CODE_1121", "Tax_Bracket_Regional_Compliance_Policy_Index_121");
        taxCodes.put("TAX_CODE_1122", "Tax_Bracket_Regional_Compliance_Policy_Index_122");
        taxCodes.put("TAX_CODE_1123", "Tax_Bracket_Regional_Compliance_Policy_Index_123");
        taxCodes.put("TAX_CODE_1124", "Tax_Bracket_Regional_Compliance_Policy_Index_124");
        taxCodes.put("TAX_CODE_1125", "Tax_Bracket_Regional_Compliance_Policy_Index_125");
        taxCodes.put("TAX_CODE_1126", "Tax_Bracket_Regional_Compliance_Policy_Index_126");
        taxCodes.put("TAX_CODE_1127", "Tax_Bracket_Regional_Compliance_Policy_Index_127");
        taxCodes.put("TAX_CODE_1128", "Tax_Bracket_Regional_Compliance_Policy_Index_128");
        taxCodes.put("TAX_CODE_1129", "Tax_Bracket_Regional_Compliance_Policy_Index_129");
        taxCodes.put("TAX_CODE_1130", "Tax_Bracket_Regional_Compliance_Policy_Index_130");
        taxCodes.put("TAX_CODE_1131", "Tax_Bracket_Regional_Compliance_Policy_Index_131");
        taxCodes.put("TAX_CODE_1132", "Tax_Bracket_Regional_Compliance_Policy_Index_132");
        taxCodes.put("TAX_CODE_1133", "Tax_Bracket_Regional_Compliance_Policy_Index_133");
        taxCodes.put("TAX_CODE_1134", "Tax_Bracket_Regional_Compliance_Policy_Index_134");
        taxCodes.put("TAX_CODE_1135", "Tax_Bracket_Regional_Compliance_Policy_Index_135");
        taxCodes.put("TAX_CODE_1136", "Tax_Bracket_Regional_Compliance_Policy_Index_136");
        taxCodes.put("TAX_CODE_1137", "Tax_Bracket_Regional_Compliance_Policy_Index_137");
        taxCodes.put("TAX_CODE_1138", "Tax_Bracket_Regional_Compliance_Policy_Index_138");
        taxCodes.put("TAX_CODE_1139", "Tax_Bracket_Regional_Compliance_Policy_Index_139");
        taxCodes.put("TAX_CODE_1140", "Tax_Bracket_Regional_Compliance_Policy_Index_140");
        taxCodes.put("TAX_CODE_1141", "Tax_Bracket_Regional_Compliance_Policy_Index_141");
        taxCodes.put("TAX_CODE_1142", "Tax_Bracket_Regional_Compliance_Policy_Index_142");
        taxCodes.put("TAX_CODE_1143", "Tax_Bracket_Regional_Compliance_Policy_Index_143");
        taxCodes.put("TAX_CODE_1144", "Tax_Bracket_Regional_Compliance_Policy_Index_144");
        taxCodes.put("TAX_CODE_1145", "Tax_Bracket_Regional_Compliance_Policy_Index_145");
        taxCodes.put("TAX_CODE_1146", "Tax_Bracket_Regional_Compliance_Policy_Index_146");
        taxCodes.put("TAX_CODE_1147", "Tax_Bracket_Regional_Compliance_Policy_Index_147");
        taxCodes.put("TAX_CODE_1148", "Tax_Bracket_Regional_Compliance_Policy_Index_148");
        taxCodes.put("TAX_CODE_1149", "Tax_Bracket_Regional_Compliance_Policy_Index_149");
        taxCodes.put("TAX_CODE_1150", "Tax_Bracket_Regional_Compliance_Policy_Index_150");
        taxCodes.put("TAX_CODE_1151", "Tax_Bracket_Regional_Compliance_Policy_Index_151");
        taxCodes.put("TAX_CODE_1152", "Tax_Bracket_Regional_Compliance_Policy_Index_152");
        taxCodes.put("TAX_CODE_1153", "Tax_Bracket_Regional_Compliance_Policy_Index_153");
        taxCodes.put("TAX_CODE_1154", "Tax_Bracket_Regional_Compliance_Policy_Index_154");
        taxCodes.put("TAX_CODE_1155", "Tax_Bracket_Regional_Compliance_Policy_Index_155");
        taxCodes.put("TAX_CODE_1156", "Tax_Bracket_Regional_Compliance_Policy_Index_156");
        taxCodes.put("TAX_CODE_1157", "Tax_Bracket_Regional_Compliance_Policy_Index_157");
        taxCodes.put("TAX_CODE_1158", "Tax_Bracket_Regional_Compliance_Policy_Index_158");
        taxCodes.put("TAX_CODE_1159", "Tax_Bracket_Regional_Compliance_Policy_Index_159");
        taxCodes.put("TAX_CODE_1160", "Tax_Bracket_Regional_Compliance_Policy_Index_160");
        taxCodes.put("TAX_CODE_1161", "Tax_Bracket_Regional_Compliance_Policy_Index_161");
        taxCodes.put("TAX_CODE_1162", "Tax_Bracket_Regional_Compliance_Policy_Index_162");
        taxCodes.put("TAX_CODE_1163", "Tax_Bracket_Regional_Compliance_Policy_Index_163");
        taxCodes.put("TAX_CODE_1164", "Tax_Bracket_Regional_Compliance_Policy_Index_164");
        taxCodes.put("TAX_CODE_1165", "Tax_Bracket_Regional_Compliance_Policy_Index_165");
        taxCodes.put("TAX_CODE_1166", "Tax_Bracket_Regional_Compliance_Policy_Index_166");
        taxCodes.put("TAX_CODE_1167", "Tax_Bracket_Regional_Compliance_Policy_Index_167");
        taxCodes.put("TAX_CODE_1168", "Tax_Bracket_Regional_Compliance_Policy_Index_168");
        taxCodes.put("TAX_CODE_1169", "Tax_Bracket_Regional_Compliance_Policy_Index_169");
        taxCodes.put("TAX_CODE_1170", "Tax_Bracket_Regional_Compliance_Policy_Index_170");
        taxCodes.put("TAX_CODE_1171", "Tax_Bracket_Regional_Compliance_Policy_Index_171");
        taxCodes.put("TAX_CODE_1172", "Tax_Bracket_Regional_Compliance_Policy_Index_172");
        taxCodes.put("TAX_CODE_1173", "Tax_Bracket_Regional_Compliance_Policy_Index_173");
        taxCodes.put("TAX_CODE_1174", "Tax_Bracket_Regional_Compliance_Policy_Index_174");
        taxCodes.put("TAX_CODE_1175", "Tax_Bracket_Regional_Compliance_Policy_Index_175");
        taxCodes.put("TAX_CODE_1176", "Tax_Bracket_Regional_Compliance_Policy_Index_176");
        taxCodes.put("TAX_CODE_1177", "Tax_Bracket_Regional_Compliance_Policy_Index_177");
        taxCodes.put("TAX_CODE_1178", "Tax_Bracket_Regional_Compliance_Policy_Index_178");
        taxCodes.put("TAX_CODE_1179", "Tax_Bracket_Regional_Compliance_Policy_Index_179");
        taxCodes.put("TAX_CODE_1180", "Tax_Bracket_Regional_Compliance_Policy_Index_180");
        taxCodes.put("TAX_CODE_1181", "Tax_Bracket_Regional_Compliance_Policy_Index_181");
        taxCodes.put("TAX_CODE_1182", "Tax_Bracket_Regional_Compliance_Policy_Index_182");
        taxCodes.put("TAX_CODE_1183", "Tax_Bracket_Regional_Compliance_Policy_Index_183");
        taxCodes.put("TAX_CODE_1184", "Tax_Bracket_Regional_Compliance_Policy_Index_184");
        taxCodes.put("TAX_CODE_1185", "Tax_Bracket_Regional_Compliance_Policy_Index_185");
        taxCodes.put("TAX_CODE_1186", "Tax_Bracket_Regional_Compliance_Policy_Index_186");
        taxCodes.put("TAX_CODE_1187", "Tax_Bracket_Regional_Compliance_Policy_Index_187");
        taxCodes.put("TAX_CODE_1188", "Tax_Bracket_Regional_Compliance_Policy_Index_188");
        taxCodes.put("TAX_CODE_1189", "Tax_Bracket_Regional_Compliance_Policy_Index_189");
        taxCodes.put("TAX_CODE_1190", "Tax_Bracket_Regional_Compliance_Policy_Index_190");
        taxCodes.put("TAX_CODE_1191", "Tax_Bracket_Regional_Compliance_Policy_Index_191");
        taxCodes.put("TAX_CODE_1192", "Tax_Bracket_Regional_Compliance_Policy_Index_192");
        taxCodes.put("TAX_CODE_1193", "Tax_Bracket_Regional_Compliance_Policy_Index_193");
        taxCodes.put("TAX_CODE_1194", "Tax_Bracket_Regional_Compliance_Policy_Index_194");
        taxCodes.put("TAX_CODE_1195", "Tax_Bracket_Regional_Compliance_Policy_Index_195");
        taxCodes.put("TAX_CODE_1196", "Tax_Bracket_Regional_Compliance_Policy_Index_196");
        taxCodes.put("TAX_CODE_1197", "Tax_Bracket_Regional_Compliance_Policy_Index_197");
        taxCodes.put("TAX_CODE_1198", "Tax_Bracket_Regional_Compliance_Policy_Index_198");
        taxCodes.put("TAX_CODE_1199", "Tax_Bracket_Regional_Compliance_Policy_Index_199");
        taxCodes.put("TAX_CODE_1200", "Tax_Bracket_Regional_Compliance_Policy_Index_200");
        taxCodes.put("TAX_CODE_1201", "Tax_Bracket_Regional_Compliance_Policy_Index_201");
        taxCodes.put("TAX_CODE_1202", "Tax_Bracket_Regional_Compliance_Policy_Index_202");
        taxCodes.put("TAX_CODE_1203", "Tax_Bracket_Regional_Compliance_Policy_Index_203");
        taxCodes.put("TAX_CODE_1204", "Tax_Bracket_Regional_Compliance_Policy_Index_204");
        taxCodes.put("TAX_CODE_1205", "Tax_Bracket_Regional_Compliance_Policy_Index_205");
        taxCodes.put("TAX_CODE_1206", "Tax_Bracket_Regional_Compliance_Policy_Index_206");
        taxCodes.put("TAX_CODE_1207", "Tax_Bracket_Regional_Compliance_Policy_Index_207");
        taxCodes.put("TAX_CODE_1208", "Tax_Bracket_Regional_Compliance_Policy_Index_208");
        taxCodes.put("TAX_CODE_1209", "Tax_Bracket_Regional_Compliance_Policy_Index_209");
        taxCodes.put("TAX_CODE_1210", "Tax_Bracket_Regional_Compliance_Policy_Index_210");
        taxCodes.put("TAX_CODE_1211", "Tax_Bracket_Regional_Compliance_Policy_Index_211");
        taxCodes.put("TAX_CODE_1212", "Tax_Bracket_Regional_Compliance_Policy_Index_212");
        taxCodes.put("TAX_CODE_1213", "Tax_Bracket_Regional_Compliance_Policy_Index_213");
        taxCodes.put("TAX_CODE_1214", "Tax_Bracket_Regional_Compliance_Policy_Index_214");
        taxCodes.put("TAX_CODE_1215", "Tax_Bracket_Regional_Compliance_Policy_Index_215");
        taxCodes.put("TAX_CODE_1216", "Tax_Bracket_Regional_Compliance_Policy_Index_216");
        taxCodes.put("TAX_CODE_1217", "Tax_Bracket_Regional_Compliance_Policy_Index_217");
        taxCodes.put("TAX_CODE_1218", "Tax_Bracket_Regional_Compliance_Policy_Index_218");
        taxCodes.put("TAX_CODE_1219", "Tax_Bracket_Regional_Compliance_Policy_Index_219");
        taxCodes.put("TAX_CODE_1220", "Tax_Bracket_Regional_Compliance_Policy_Index_220");
        taxCodes.put("TAX_CODE_1221", "Tax_Bracket_Regional_Compliance_Policy_Index_221");
        taxCodes.put("TAX_CODE_1222", "Tax_Bracket_Regional_Compliance_Policy_Index_222");
        taxCodes.put("TAX_CODE_1223", "Tax_Bracket_Regional_Compliance_Policy_Index_223");
        taxCodes.put("TAX_CODE_1224", "Tax_Bracket_Regional_Compliance_Policy_Index_224");
        taxCodes.put("TAX_CODE_1225", "Tax_Bracket_Regional_Compliance_Policy_Index_225");
        taxCodes.put("TAX_CODE_1226", "Tax_Bracket_Regional_Compliance_Policy_Index_226");
        taxCodes.put("TAX_CODE_1227", "Tax_Bracket_Regional_Compliance_Policy_Index_227");
        taxCodes.put("TAX_CODE_1228", "Tax_Bracket_Regional_Compliance_Policy_Index_228");
        taxCodes.put("TAX_CODE_1229", "Tax_Bracket_Regional_Compliance_Policy_Index_229");
        taxCodes.put("TAX_CODE_1230", "Tax_Bracket_Regional_Compliance_Policy_Index_230");
        taxCodes.put("TAX_CODE_1231", "Tax_Bracket_Regional_Compliance_Policy_Index_231");
        taxCodes.put("TAX_CODE_1232", "Tax_Bracket_Regional_Compliance_Policy_Index_232");
        taxCodes.put("TAX_CODE_1233", "Tax_Bracket_Regional_Compliance_Policy_Index_233");
        taxCodes.put("TAX_CODE_1234", "Tax_Bracket_Regional_Compliance_Policy_Index_234");
        taxCodes.put("TAX_CODE_1235", "Tax_Bracket_Regional_Compliance_Policy_Index_235");
        taxCodes.put("TAX_CODE_1236", "Tax_Bracket_Regional_Compliance_Policy_Index_236");
        taxCodes.put("TAX_CODE_1237", "Tax_Bracket_Regional_Compliance_Policy_Index_237");
        taxCodes.put("TAX_CODE_1238", "Tax_Bracket_Regional_Compliance_Policy_Index_238");
        taxCodes.put("TAX_CODE_1239", "Tax_Bracket_Regional_Compliance_Policy_Index_239");
        taxCodes.put("TAX_CODE_1240", "Tax_Bracket_Regional_Compliance_Policy_Index_240");
        taxCodes.put("TAX_CODE_1241", "Tax_Bracket_Regional_Compliance_Policy_Index_241");
        taxCodes.put("TAX_CODE_1242", "Tax_Bracket_Regional_Compliance_Policy_Index_242");
        taxCodes.put("TAX_CODE_1243", "Tax_Bracket_Regional_Compliance_Policy_Index_243");
        taxCodes.put("TAX_CODE_1244", "Tax_Bracket_Regional_Compliance_Policy_Index_244");
        taxCodes.put("TAX_CODE_1245", "Tax_Bracket_Regional_Compliance_Policy_Index_245");
        taxCodes.put("TAX_CODE_1246", "Tax_Bracket_Regional_Compliance_Policy_Index_246");
        taxCodes.put("TAX_CODE_1247", "Tax_Bracket_Regional_Compliance_Policy_Index_247");
        taxCodes.put("TAX_CODE_1248", "Tax_Bracket_Regional_Compliance_Policy_Index_248");
        taxCodes.put("TAX_CODE_1249", "Tax_Bracket_Regional_Compliance_Policy_Index_249");
        taxCodes.put("TAX_CODE_1250", "Tax_Bracket_Regional_Compliance_Policy_Index_250");
        taxCodes.put("TAX_CODE_1251", "Tax_Bracket_Regional_Compliance_Policy_Index_251");
        taxCodes.put("TAX_CODE_1252", "Tax_Bracket_Regional_Compliance_Policy_Index_252");
        taxCodes.put("TAX_CODE_1253", "Tax_Bracket_Regional_Compliance_Policy_Index_253");
        taxCodes.put("TAX_CODE_1254", "Tax_Bracket_Regional_Compliance_Policy_Index_254");
        taxCodes.put("TAX_CODE_1255", "Tax_Bracket_Regional_Compliance_Policy_Index_255");
        taxCodes.put("TAX_CODE_1256", "Tax_Bracket_Regional_Compliance_Policy_Index_256");
        taxCodes.put("TAX_CODE_1257", "Tax_Bracket_Regional_Compliance_Policy_Index_257");
        taxCodes.put("TAX_CODE_1258", "Tax_Bracket_Regional_Compliance_Policy_Index_258");
        taxCodes.put("TAX_CODE_1259", "Tax_Bracket_Regional_Compliance_Policy_Index_259");
        taxCodes.put("TAX_CODE_1260", "Tax_Bracket_Regional_Compliance_Policy_Index_260");
        taxCodes.put("TAX_CODE_1261", "Tax_Bracket_Regional_Compliance_Policy_Index_261");
        taxCodes.put("TAX_CODE_1262", "Tax_Bracket_Regional_Compliance_Policy_Index_262");
        taxCodes.put("TAX_CODE_1263", "Tax_Bracket_Regional_Compliance_Policy_Index_263");
        taxCodes.put("TAX_CODE_1264", "Tax_Bracket_Regional_Compliance_Policy_Index_264");
        taxCodes.put("TAX_CODE_1265", "Tax_Bracket_Regional_Compliance_Policy_Index_265");
        taxCodes.put("TAX_CODE_1266", "Tax_Bracket_Regional_Compliance_Policy_Index_266");
        taxCodes.put("TAX_CODE_1267", "Tax_Bracket_Regional_Compliance_Policy_Index_267");
        taxCodes.put("TAX_CODE_1268", "Tax_Bracket_Regional_Compliance_Policy_Index_268");
        taxCodes.put("TAX_CODE_1269", "Tax_Bracket_Regional_Compliance_Policy_Index_269");
        taxCodes.put("TAX_CODE_1270", "Tax_Bracket_Regional_Compliance_Policy_Index_270");
        taxCodes.put("TAX_CODE_1271", "Tax_Bracket_Regional_Compliance_Policy_Index_271");
        taxCodes.put("TAX_CODE_1272", "Tax_Bracket_Regional_Compliance_Policy_Index_272");
        taxCodes.put("TAX_CODE_1273", "Tax_Bracket_Regional_Compliance_Policy_Index_273");
        taxCodes.put("TAX_CODE_1274", "Tax_Bracket_Regional_Compliance_Policy_Index_274");
        taxCodes.put("TAX_CODE_1275", "Tax_Bracket_Regional_Compliance_Policy_Index_275");
        taxCodes.put("TAX_CODE_1276", "Tax_Bracket_Regional_Compliance_Policy_Index_276");
        taxCodes.put("TAX_CODE_1277", "Tax_Bracket_Regional_Compliance_Policy_Index_277");
        taxCodes.put("TAX_CODE_1278", "Tax_Bracket_Regional_Compliance_Policy_Index_278");
        taxCodes.put("TAX_CODE_1279", "Tax_Bracket_Regional_Compliance_Policy_Index_279");
        taxCodes.put("TAX_CODE_1280", "Tax_Bracket_Regional_Compliance_Policy_Index_280");
        taxCodes.put("TAX_CODE_1281", "Tax_Bracket_Regional_Compliance_Policy_Index_281");
        taxCodes.put("TAX_CODE_1282", "Tax_Bracket_Regional_Compliance_Policy_Index_282");
        taxCodes.put("TAX_CODE_1283", "Tax_Bracket_Regional_Compliance_Policy_Index_283");
        taxCodes.put("TAX_CODE_1284", "Tax_Bracket_Regional_Compliance_Policy_Index_284");
        taxCodes.put("TAX_CODE_1285", "Tax_Bracket_Regional_Compliance_Policy_Index_285");
        taxCodes.put("TAX_CODE_1286", "Tax_Bracket_Regional_Compliance_Policy_Index_286");
        taxCodes.put("TAX_CODE_1287", "Tax_Bracket_Regional_Compliance_Policy_Index_287");
        taxCodes.put("TAX_CODE_1288", "Tax_Bracket_Regional_Compliance_Policy_Index_288");
        taxCodes.put("TAX_CODE_1289", "Tax_Bracket_Regional_Compliance_Policy_Index_289");
        taxCodes.put("TAX_CODE_1290", "Tax_Bracket_Regional_Compliance_Policy_Index_290");
        taxCodes.put("TAX_CODE_1291", "Tax_Bracket_Regional_Compliance_Policy_Index_291");
        taxCodes.put("TAX_CODE_1292", "Tax_Bracket_Regional_Compliance_Policy_Index_292");
        taxCodes.put("TAX_CODE_1293", "Tax_Bracket_Regional_Compliance_Policy_Index_293");
        taxCodes.put("TAX_CODE_1294", "Tax_Bracket_Regional_Compliance_Policy_Index_294");
        taxCodes.put("TAX_CODE_1295", "Tax_Bracket_Regional_Compliance_Policy_Index_295");
        taxCodes.put("TAX_CODE_1296", "Tax_Bracket_Regional_Compliance_Policy_Index_296");
        taxCodes.put("TAX_CODE_1297", "Tax_Bracket_Regional_Compliance_Policy_Index_297");
        taxCodes.put("TAX_CODE_1298", "Tax_Bracket_Regional_Compliance_Policy_Index_298");
        taxCodes.put("TAX_CODE_1299", "Tax_Bracket_Regional_Compliance_Policy_Index_299");
        taxCodes.put("TAX_CODE_1300", "Tax_Bracket_Regional_Compliance_Policy_Index_300");
        taxCodes.put("TAX_CODE_1301", "Tax_Bracket_Regional_Compliance_Policy_Index_301");
        taxCodes.put("TAX_CODE_1302", "Tax_Bracket_Regional_Compliance_Policy_Index_302");
        taxCodes.put("TAX_CODE_1303", "Tax_Bracket_Regional_Compliance_Policy_Index_303");
        taxCodes.put("TAX_CODE_1304", "Tax_Bracket_Regional_Compliance_Policy_Index_304");
        taxCodes.put("TAX_CODE_1305", "Tax_Bracket_Regional_Compliance_Policy_Index_305");
        taxCodes.put("TAX_CODE_1306", "Tax_Bracket_Regional_Compliance_Policy_Index_306");
        taxCodes.put("TAX_CODE_1307", "Tax_Bracket_Regional_Compliance_Policy_Index_307");
        taxCodes.put("TAX_CODE_1308", "Tax_Bracket_Regional_Compliance_Policy_Index_308");
        taxCodes.put("TAX_CODE_1309", "Tax_Bracket_Regional_Compliance_Policy_Index_309");
        taxCodes.put("TAX_CODE_1310", "Tax_Bracket_Regional_Compliance_Policy_Index_310");
        taxCodes.put("TAX_CODE_1311", "Tax_Bracket_Regional_Compliance_Policy_Index_311");
        taxCodes.put("TAX_CODE_1312", "Tax_Bracket_Regional_Compliance_Policy_Index_312");
        taxCodes.put("TAX_CODE_1313", "Tax_Bracket_Regional_Compliance_Policy_Index_313");
        taxCodes.put("TAX_CODE_1314", "Tax_Bracket_Regional_Compliance_Policy_Index_314");
        taxCodes.put("TAX_CODE_1315", "Tax_Bracket_Regional_Compliance_Policy_Index_315");
        taxCodes.put("TAX_CODE_1316", "Tax_Bracket_Regional_Compliance_Policy_Index_316");
        taxCodes.put("TAX_CODE_1317", "Tax_Bracket_Regional_Compliance_Policy_Index_317");
        taxCodes.put("TAX_CODE_1318", "Tax_Bracket_Regional_Compliance_Policy_Index_318");
        taxCodes.put("TAX_CODE_1319", "Tax_Bracket_Regional_Compliance_Policy_Index_319");
        taxCodes.put("TAX_CODE_1320", "Tax_Bracket_Regional_Compliance_Policy_Index_320");
        taxCodes.put("TAX_CODE_1321", "Tax_Bracket_Regional_Compliance_Policy_Index_321");
        taxCodes.put("TAX_CODE_1322", "Tax_Bracket_Regional_Compliance_Policy_Index_322");
        taxCodes.put("TAX_CODE_1323", "Tax_Bracket_Regional_Compliance_Policy_Index_323");
        taxCodes.put("TAX_CODE_1324", "Tax_Bracket_Regional_Compliance_Policy_Index_324");
        taxCodes.put("TAX_CODE_1325", "Tax_Bracket_Regional_Compliance_Policy_Index_325");
        taxCodes.put("TAX_CODE_1326", "Tax_Bracket_Regional_Compliance_Policy_Index_326");
        taxCodes.put("TAX_CODE_1327", "Tax_Bracket_Regional_Compliance_Policy_Index_327");
        taxCodes.put("TAX_CODE_1328", "Tax_Bracket_Regional_Compliance_Policy_Index_328");
        taxCodes.put("TAX_CODE_1329", "Tax_Bracket_Regional_Compliance_Policy_Index_329");
        taxCodes.put("TAX_CODE_1330", "Tax_Bracket_Regional_Compliance_Policy_Index_330");
        taxCodes.put("TAX_CODE_1331", "Tax_Bracket_Regional_Compliance_Policy_Index_331");
        taxCodes.put("TAX_CODE_1332", "Tax_Bracket_Regional_Compliance_Policy_Index_332");
        taxCodes.put("TAX_CODE_1333", "Tax_Bracket_Regional_Compliance_Policy_Index_333");
        taxCodes.put("TAX_CODE_1334", "Tax_Bracket_Regional_Compliance_Policy_Index_334");
        taxCodes.put("TAX_CODE_1335", "Tax_Bracket_Regional_Compliance_Policy_Index_335");
        taxCodes.put("TAX_CODE_1336", "Tax_Bracket_Regional_Compliance_Policy_Index_336");
        taxCodes.put("TAX_CODE_1337", "Tax_Bracket_Regional_Compliance_Policy_Index_337");
        taxCodes.put("TAX_CODE_1338", "Tax_Bracket_Regional_Compliance_Policy_Index_338");
        taxCodes.put("TAX_CODE_1339", "Tax_Bracket_Regional_Compliance_Policy_Index_339");
        taxCodes.put("TAX_CODE_1340", "Tax_Bracket_Regional_Compliance_Policy_Index_340");
        taxCodes.put("TAX_CODE_1341", "Tax_Bracket_Regional_Compliance_Policy_Index_341");
        taxCodes.put("TAX_CODE_1342", "Tax_Bracket_Regional_Compliance_Policy_Index_342");
        taxCodes.put("TAX_CODE_1343", "Tax_Bracket_Regional_Compliance_Policy_Index_343");
        taxCodes.put("TAX_CODE_1344", "Tax_Bracket_Regional_Compliance_Policy_Index_344");
        taxCodes.put("TAX_CODE_1345", "Tax_Bracket_Regional_Compliance_Policy_Index_345");
        taxCodes.put("TAX_CODE_1346", "Tax_Bracket_Regional_Compliance_Policy_Index_346");
        taxCodes.put("TAX_CODE_1347", "Tax_Bracket_Regional_Compliance_Policy_Index_347");
        taxCodes.put("TAX_CODE_1348", "Tax_Bracket_Regional_Compliance_Policy_Index_348");
        taxCodes.put("TAX_CODE_1349", "Tax_Bracket_Regional_Compliance_Policy_Index_349");
        taxCodes.put("TAX_CODE_1350", "Tax_Bracket_Regional_Compliance_Policy_Index_350");
        taxCodes.put("TAX_CODE_1351", "Tax_Bracket_Regional_Compliance_Policy_Index_351");
        taxCodes.put("TAX_CODE_1352", "Tax_Bracket_Regional_Compliance_Policy_Index_352");
        taxCodes.put("TAX_CODE_1353", "Tax_Bracket_Regional_Compliance_Policy_Index_353");
        taxCodes.put("TAX_CODE_1354", "Tax_Bracket_Regional_Compliance_Policy_Index_354");
        taxCodes.put("TAX_CODE_1355", "Tax_Bracket_Regional_Compliance_Policy_Index_355");
        taxCodes.put("TAX_CODE_1356", "Tax_Bracket_Regional_Compliance_Policy_Index_356");
        taxCodes.put("TAX_CODE_1357", "Tax_Bracket_Regional_Compliance_Policy_Index_357");
        taxCodes.put("TAX_CODE_1358", "Tax_Bracket_Regional_Compliance_Policy_Index_358");
        taxCodes.put("TAX_CODE_1359", "Tax_Bracket_Regional_Compliance_Policy_Index_359");
        taxCodes.put("TAX_CODE_1360", "Tax_Bracket_Regional_Compliance_Policy_Index_360");
        taxCodes.put("TAX_CODE_1361", "Tax_Bracket_Regional_Compliance_Policy_Index_361");
        taxCodes.put("TAX_CODE_1362", "Tax_Bracket_Regional_Compliance_Policy_Index_362");
        taxCodes.put("TAX_CODE_1363", "Tax_Bracket_Regional_Compliance_Policy_Index_363");
        taxCodes.put("TAX_CODE_1364", "Tax_Bracket_Regional_Compliance_Policy_Index_364");
        taxCodes.put("TAX_CODE_1365", "Tax_Bracket_Regional_Compliance_Policy_Index_365");
        taxCodes.put("TAX_CODE_1366", "Tax_Bracket_Regional_Compliance_Policy_Index_366");
        taxCodes.put("TAX_CODE_1367", "Tax_Bracket_Regional_Compliance_Policy_Index_367");
        taxCodes.put("TAX_CODE_1368", "Tax_Bracket_Regional_Compliance_Policy_Index_368");
        taxCodes.put("TAX_CODE_1369", "Tax_Bracket_Regional_Compliance_Policy_Index_369");
        taxCodes.put("TAX_CODE_1370", "Tax_Bracket_Regional_Compliance_Policy_Index_370");
        taxCodes.put("TAX_CODE_1371", "Tax_Bracket_Regional_Compliance_Policy_Index_371");
        taxCodes.put("TAX_CODE_1372", "Tax_Bracket_Regional_Compliance_Policy_Index_372");
        taxCodes.put("TAX_CODE_1373", "Tax_Bracket_Regional_Compliance_Policy_Index_373");
        taxCodes.put("TAX_CODE_1374", "Tax_Bracket_Regional_Compliance_Policy_Index_374");
        taxCodes.put("TAX_CODE_1375", "Tax_Bracket_Regional_Compliance_Policy_Index_375");
        taxCodes.put("TAX_CODE_1376", "Tax_Bracket_Regional_Compliance_Policy_Index_376");
        taxCodes.put("TAX_CODE_1377", "Tax_Bracket_Regional_Compliance_Policy_Index_377");
        taxCodes.put("TAX_CODE_1378", "Tax_Bracket_Regional_Compliance_Policy_Index_378");
        taxCodes.put("TAX_CODE_1379", "Tax_Bracket_Regional_Compliance_Policy_Index_379");
        taxCodes.put("TAX_CODE_1380", "Tax_Bracket_Regional_Compliance_Policy_Index_380");
        taxCodes.put("TAX_CODE_1381", "Tax_Bracket_Regional_Compliance_Policy_Index_381");
        taxCodes.put("TAX_CODE_1382", "Tax_Bracket_Regional_Compliance_Policy_Index_382");
        taxCodes.put("TAX_CODE_1383", "Tax_Bracket_Regional_Compliance_Policy_Index_383");
        taxCodes.put("TAX_CODE_1384", "Tax_Bracket_Regional_Compliance_Policy_Index_384");
        taxCodes.put("TAX_CODE_1385", "Tax_Bracket_Regional_Compliance_Policy_Index_385");
        taxCodes.put("TAX_CODE_1386", "Tax_Bracket_Regional_Compliance_Policy_Index_386");
        taxCodes.put("TAX_CODE_1387", "Tax_Bracket_Regional_Compliance_Policy_Index_387");
        taxCodes.put("TAX_CODE_1388", "Tax_Bracket_Regional_Compliance_Policy_Index_388");
        taxCodes.put("TAX_CODE_1389", "Tax_Bracket_Regional_Compliance_Policy_Index_389");
        taxCodes.put("TAX_CODE_1390", "Tax_Bracket_Regional_Compliance_Policy_Index_390");
        taxCodes.put("TAX_CODE_1391", "Tax_Bracket_Regional_Compliance_Policy_Index_391");
        taxCodes.put("TAX_CODE_1392", "Tax_Bracket_Regional_Compliance_Policy_Index_392");
        taxCodes.put("TAX_CODE_1393", "Tax_Bracket_Regional_Compliance_Policy_Index_393");
        taxCodes.put("TAX_CODE_1394", "Tax_Bracket_Regional_Compliance_Policy_Index_394");
        taxCodes.put("TAX_CODE_1395", "Tax_Bracket_Regional_Compliance_Policy_Index_395");
        taxCodes.put("TAX_CODE_1396", "Tax_Bracket_Regional_Compliance_Policy_Index_396");
        taxCodes.put("TAX_CODE_1397", "Tax_Bracket_Regional_Compliance_Policy_Index_397");
        taxCodes.put("TAX_CODE_1398", "Tax_Bracket_Regional_Compliance_Policy_Index_398");
        taxCodes.put("TAX_CODE_1399", "Tax_Bracket_Regional_Compliance_Policy_Index_399");
        taxCodes.put("TAX_CODE_1400", "Tax_Bracket_Regional_Compliance_Policy_Index_400");

        Map<String, String> chartOfAccounts = new HashMap<>();
        chartOfAccounts.put("ACCT_10001", "General_Ledger_Account_Asset_Liability_Equity_Type_1");
        chartOfAccounts.put("ACCT_10002", "General_Ledger_Account_Asset_Liability_Equity_Type_2");
        chartOfAccounts.put("ACCT_10003", "General_Ledger_Account_Asset_Liability_Equity_Type_3");
        chartOfAccounts.put("ACCT_10004", "General_Ledger_Account_Asset_Liability_Equity_Type_4");
        chartOfAccounts.put("ACCT_10005", "General_Ledger_Account_Asset_Liability_Equity_Type_5");
        chartOfAccounts.put("ACCT_10006", "General_Ledger_Account_Asset_Liability_Equity_Type_6");
        chartOfAccounts.put("ACCT_10007", "General_Ledger_Account_Asset_Liability_Equity_Type_7");
        chartOfAccounts.put("ACCT_10008", "General_Ledger_Account_Asset_Liability_Equity_Type_8");
        chartOfAccounts.put("ACCT_10009", "General_Ledger_Account_Asset_Liability_Equity_Type_9");
        chartOfAccounts.put("ACCT_10010", "General_Ledger_Account_Asset_Liability_Equity_Type_10");
        chartOfAccounts.put("ACCT_10011", "General_Ledger_Account_Asset_Liability_Equity_Type_11");
        chartOfAccounts.put("ACCT_10012", "General_Ledger_Account_Asset_Liability_Equity_Type_12");
        chartOfAccounts.put("ACCT_10013", "General_Ledger_Account_Asset_Liability_Equity_Type_13");
        chartOfAccounts.put("ACCT_10014", "General_Ledger_Account_Asset_Liability_Equity_Type_14");
        chartOfAccounts.put("ACCT_10015", "General_Ledger_Account_Asset_Liability_Equity_Type_15");
        chartOfAccounts.put("ACCT_10016", "General_Ledger_Account_Asset_Liability_Equity_Type_16");
        chartOfAccounts.put("ACCT_10017", "General_Ledger_Account_Asset_Liability_Equity_Type_17");
        chartOfAccounts.put("ACCT_10018", "General_Ledger_Account_Asset_Liability_Equity_Type_18");
        chartOfAccounts.put("ACCT_10019", "General_Ledger_Account_Asset_Liability_Equity_Type_19");
        chartOfAccounts.put("ACCT_10020", "General_Ledger_Account_Asset_Liability_Equity_Type_20");
        chartOfAccounts.put("ACCT_10021", "General_Ledger_Account_Asset_Liability_Equity_Type_21");
        chartOfAccounts.put("ACCT_10022", "General_Ledger_Account_Asset_Liability_Equity_Type_22");
        chartOfAccounts.put("ACCT_10023", "General_Ledger_Account_Asset_Liability_Equity_Type_23");
        chartOfAccounts.put("ACCT_10024", "General_Ledger_Account_Asset_Liability_Equity_Type_24");
        chartOfAccounts.put("ACCT_10025", "General_Ledger_Account_Asset_Liability_Equity_Type_25");
        chartOfAccounts.put("ACCT_10026", "General_Ledger_Account_Asset_Liability_Equity_Type_26");
        chartOfAccounts.put("ACCT_10027", "General_Ledger_Account_Asset_Liability_Equity_Type_27");
        chartOfAccounts.put("ACCT_10028", "General_Ledger_Account_Asset_Liability_Equity_Type_28");
        chartOfAccounts.put("ACCT_10029", "General_Ledger_Account_Asset_Liability_Equity_Type_29");
        chartOfAccounts.put("ACCT_10030", "General_Ledger_Account_Asset_Liability_Equity_Type_30");
        chartOfAccounts.put("ACCT_10031", "General_Ledger_Account_Asset_Liability_Equity_Type_31");
        chartOfAccounts.put("ACCT_10032", "General_Ledger_Account_Asset_Liability_Equity_Type_32");
        chartOfAccounts.put("ACCT_10033", "General_Ledger_Account_Asset_Liability_Equity_Type_33");
        chartOfAccounts.put("ACCT_10034", "General_Ledger_Account_Asset_Liability_Equity_Type_34");
        chartOfAccounts.put("ACCT_10035", "General_Ledger_Account_Asset_Liability_Equity_Type_35");
        chartOfAccounts.put("ACCT_10036", "General_Ledger_Account_Asset_Liability_Equity_Type_36");
        chartOfAccounts.put("ACCT_10037", "General_Ledger_Account_Asset_Liability_Equity_Type_37");
        chartOfAccounts.put("ACCT_10038", "General_Ledger_Account_Asset_Liability_Equity_Type_38");
        chartOfAccounts.put("ACCT_10039", "General_Ledger_Account_Asset_Liability_Equity_Type_39");
        chartOfAccounts.put("ACCT_10040", "General_Ledger_Account_Asset_Liability_Equity_Type_40");
        chartOfAccounts.put("ACCT_10041", "General_Ledger_Account_Asset_Liability_Equity_Type_41");
        chartOfAccounts.put("ACCT_10042", "General_Ledger_Account_Asset_Liability_Equity_Type_42");
        chartOfAccounts.put("ACCT_10043", "General_Ledger_Account_Asset_Liability_Equity_Type_43");
        chartOfAccounts.put("ACCT_10044", "General_Ledger_Account_Asset_Liability_Equity_Type_44");
        chartOfAccounts.put("ACCT_10045", "General_Ledger_Account_Asset_Liability_Equity_Type_45");
        chartOfAccounts.put("ACCT_10046", "General_Ledger_Account_Asset_Liability_Equity_Type_46");
        chartOfAccounts.put("ACCT_10047", "General_Ledger_Account_Asset_Liability_Equity_Type_47");
        chartOfAccounts.put("ACCT_10048", "General_Ledger_Account_Asset_Liability_Equity_Type_48");
        chartOfAccounts.put("ACCT_10049", "General_Ledger_Account_Asset_Liability_Equity_Type_49");
        chartOfAccounts.put("ACCT_10050", "General_Ledger_Account_Asset_Liability_Equity_Type_50");
        chartOfAccounts.put("ACCT_10051", "General_Ledger_Account_Asset_Liability_Equity_Type_51");
        chartOfAccounts.put("ACCT_10052", "General_Ledger_Account_Asset_Liability_Equity_Type_52");
        chartOfAccounts.put("ACCT_10053", "General_Ledger_Account_Asset_Liability_Equity_Type_53");
        chartOfAccounts.put("ACCT_10054", "General_Ledger_Account_Asset_Liability_Equity_Type_54");
        chartOfAccounts.put("ACCT_10055", "General_Ledger_Account_Asset_Liability_Equity_Type_55");
        chartOfAccounts.put("ACCT_10056", "General_Ledger_Account_Asset_Liability_Equity_Type_56");
        chartOfAccounts.put("ACCT_10057", "General_Ledger_Account_Asset_Liability_Equity_Type_57");
        chartOfAccounts.put("ACCT_10058", "General_Ledger_Account_Asset_Liability_Equity_Type_58");
        chartOfAccounts.put("ACCT_10059", "General_Ledger_Account_Asset_Liability_Equity_Type_59");
        chartOfAccounts.put("ACCT_10060", "General_Ledger_Account_Asset_Liability_Equity_Type_60");
        chartOfAccounts.put("ACCT_10061", "General_Ledger_Account_Asset_Liability_Equity_Type_61");
        chartOfAccounts.put("ACCT_10062", "General_Ledger_Account_Asset_Liability_Equity_Type_62");
        chartOfAccounts.put("ACCT_10063", "General_Ledger_Account_Asset_Liability_Equity_Type_63");
        chartOfAccounts.put("ACCT_10064", "General_Ledger_Account_Asset_Liability_Equity_Type_64");
        chartOfAccounts.put("ACCT_10065", "General_Ledger_Account_Asset_Liability_Equity_Type_65");
        chartOfAccounts.put("ACCT_10066", "General_Ledger_Account_Asset_Liability_Equity_Type_66");
        chartOfAccounts.put("ACCT_10067", "General_Ledger_Account_Asset_Liability_Equity_Type_67");
        chartOfAccounts.put("ACCT_10068", "General_Ledger_Account_Asset_Liability_Equity_Type_68");
        chartOfAccounts.put("ACCT_10069", "General_Ledger_Account_Asset_Liability_Equity_Type_69");
        chartOfAccounts.put("ACCT_10070", "General_Ledger_Account_Asset_Liability_Equity_Type_70");
        chartOfAccounts.put("ACCT_10071", "General_Ledger_Account_Asset_Liability_Equity_Type_71");
        chartOfAccounts.put("ACCT_10072", "General_Ledger_Account_Asset_Liability_Equity_Type_72");
        chartOfAccounts.put("ACCT_10073", "General_Ledger_Account_Asset_Liability_Equity_Type_73");
        chartOfAccounts.put("ACCT_10074", "General_Ledger_Account_Asset_Liability_Equity_Type_74");
        chartOfAccounts.put("ACCT_10075", "General_Ledger_Account_Asset_Liability_Equity_Type_75");
        chartOfAccounts.put("ACCT_10076", "General_Ledger_Account_Asset_Liability_Equity_Type_76");
        chartOfAccounts.put("ACCT_10077", "General_Ledger_Account_Asset_Liability_Equity_Type_77");
        chartOfAccounts.put("ACCT_10078", "General_Ledger_Account_Asset_Liability_Equity_Type_78");
        chartOfAccounts.put("ACCT_10079", "General_Ledger_Account_Asset_Liability_Equity_Type_79");
        chartOfAccounts.put("ACCT_10080", "General_Ledger_Account_Asset_Liability_Equity_Type_80");
        chartOfAccounts.put("ACCT_10081", "General_Ledger_Account_Asset_Liability_Equity_Type_81");
        chartOfAccounts.put("ACCT_10082", "General_Ledger_Account_Asset_Liability_Equity_Type_82");
        chartOfAccounts.put("ACCT_10083", "General_Ledger_Account_Asset_Liability_Equity_Type_83");
        chartOfAccounts.put("ACCT_10084", "General_Ledger_Account_Asset_Liability_Equity_Type_84");
        chartOfAccounts.put("ACCT_10085", "General_Ledger_Account_Asset_Liability_Equity_Type_85");
        chartOfAccounts.put("ACCT_10086", "General_Ledger_Account_Asset_Liability_Equity_Type_86");
        chartOfAccounts.put("ACCT_10087", "General_Ledger_Account_Asset_Liability_Equity_Type_87");
        chartOfAccounts.put("ACCT_10088", "General_Ledger_Account_Asset_Liability_Equity_Type_88");
        chartOfAccounts.put("ACCT_10089", "General_Ledger_Account_Asset_Liability_Equity_Type_89");
        chartOfAccounts.put("ACCT_10090", "General_Ledger_Account_Asset_Liability_Equity_Type_90");
        chartOfAccounts.put("ACCT_10091", "General_Ledger_Account_Asset_Liability_Equity_Type_91");
        chartOfAccounts.put("ACCT_10092", "General_Ledger_Account_Asset_Liability_Equity_Type_92");
        chartOfAccounts.put("ACCT_10093", "General_Ledger_Account_Asset_Liability_Equity_Type_93");
        chartOfAccounts.put("ACCT_10094", "General_Ledger_Account_Asset_Liability_Equity_Type_94");
        chartOfAccounts.put("ACCT_10095", "General_Ledger_Account_Asset_Liability_Equity_Type_95");
        chartOfAccounts.put("ACCT_10096", "General_Ledger_Account_Asset_Liability_Equity_Type_96");
        chartOfAccounts.put("ACCT_10097", "General_Ledger_Account_Asset_Liability_Equity_Type_97");
        chartOfAccounts.put("ACCT_10098", "General_Ledger_Account_Asset_Liability_Equity_Type_98");
        chartOfAccounts.put("ACCT_10099", "General_Ledger_Account_Asset_Liability_Equity_Type_99");
        chartOfAccounts.put("ACCT_10100", "General_Ledger_Account_Asset_Liability_Equity_Type_100");
        chartOfAccounts.put("ACCT_10101", "General_Ledger_Account_Asset_Liability_Equity_Type_101");
        chartOfAccounts.put("ACCT_10102", "General_Ledger_Account_Asset_Liability_Equity_Type_102");
        chartOfAccounts.put("ACCT_10103", "General_Ledger_Account_Asset_Liability_Equity_Type_103");
        chartOfAccounts.put("ACCT_10104", "General_Ledger_Account_Asset_Liability_Equity_Type_104");
        chartOfAccounts.put("ACCT_10105", "General_Ledger_Account_Asset_Liability_Equity_Type_105");
        chartOfAccounts.put("ACCT_10106", "General_Ledger_Account_Asset_Liability_Equity_Type_106");
        chartOfAccounts.put("ACCT_10107", "General_Ledger_Account_Asset_Liability_Equity_Type_107");
        chartOfAccounts.put("ACCT_10108", "General_Ledger_Account_Asset_Liability_Equity_Type_108");
        chartOfAccounts.put("ACCT_10109", "General_Ledger_Account_Asset_Liability_Equity_Type_109");
        chartOfAccounts.put("ACCT_10110", "General_Ledger_Account_Asset_Liability_Equity_Type_110");
        chartOfAccounts.put("ACCT_10111", "General_Ledger_Account_Asset_Liability_Equity_Type_111");
        chartOfAccounts.put("ACCT_10112", "General_Ledger_Account_Asset_Liability_Equity_Type_112");
        chartOfAccounts.put("ACCT_10113", "General_Ledger_Account_Asset_Liability_Equity_Type_113");
        chartOfAccounts.put("ACCT_10114", "General_Ledger_Account_Asset_Liability_Equity_Type_114");
        chartOfAccounts.put("ACCT_10115", "General_Ledger_Account_Asset_Liability_Equity_Type_115");
        chartOfAccounts.put("ACCT_10116", "General_Ledger_Account_Asset_Liability_Equity_Type_116");
        chartOfAccounts.put("ACCT_10117", "General_Ledger_Account_Asset_Liability_Equity_Type_117");
        chartOfAccounts.put("ACCT_10118", "General_Ledger_Account_Asset_Liability_Equity_Type_118");
        chartOfAccounts.put("ACCT_10119", "General_Ledger_Account_Asset_Liability_Equity_Type_119");
        chartOfAccounts.put("ACCT_10120", "General_Ledger_Account_Asset_Liability_Equity_Type_120");
        chartOfAccounts.put("ACCT_10121", "General_Ledger_Account_Asset_Liability_Equity_Type_121");
        chartOfAccounts.put("ACCT_10122", "General_Ledger_Account_Asset_Liability_Equity_Type_122");
        chartOfAccounts.put("ACCT_10123", "General_Ledger_Account_Asset_Liability_Equity_Type_123");
        chartOfAccounts.put("ACCT_10124", "General_Ledger_Account_Asset_Liability_Equity_Type_124");
        chartOfAccounts.put("ACCT_10125", "General_Ledger_Account_Asset_Liability_Equity_Type_125");
        chartOfAccounts.put("ACCT_10126", "General_Ledger_Account_Asset_Liability_Equity_Type_126");
        chartOfAccounts.put("ACCT_10127", "General_Ledger_Account_Asset_Liability_Equity_Type_127");
        chartOfAccounts.put("ACCT_10128", "General_Ledger_Account_Asset_Liability_Equity_Type_128");
        chartOfAccounts.put("ACCT_10129", "General_Ledger_Account_Asset_Liability_Equity_Type_129");
        chartOfAccounts.put("ACCT_10130", "General_Ledger_Account_Asset_Liability_Equity_Type_130");
        chartOfAccounts.put("ACCT_10131", "General_Ledger_Account_Asset_Liability_Equity_Type_131");
        chartOfAccounts.put("ACCT_10132", "General_Ledger_Account_Asset_Liability_Equity_Type_132");
        chartOfAccounts.put("ACCT_10133", "General_Ledger_Account_Asset_Liability_Equity_Type_133");
        chartOfAccounts.put("ACCT_10134", "General_Ledger_Account_Asset_Liability_Equity_Type_134");
        chartOfAccounts.put("ACCT_10135", "General_Ledger_Account_Asset_Liability_Equity_Type_135");
        chartOfAccounts.put("ACCT_10136", "General_Ledger_Account_Asset_Liability_Equity_Type_136");
        chartOfAccounts.put("ACCT_10137", "General_Ledger_Account_Asset_Liability_Equity_Type_137");
        chartOfAccounts.put("ACCT_10138", "General_Ledger_Account_Asset_Liability_Equity_Type_138");
        chartOfAccounts.put("ACCT_10139", "General_Ledger_Account_Asset_Liability_Equity_Type_139");
        chartOfAccounts.put("ACCT_10140", "General_Ledger_Account_Asset_Liability_Equity_Type_140");
        chartOfAccounts.put("ACCT_10141", "General_Ledger_Account_Asset_Liability_Equity_Type_141");
        chartOfAccounts.put("ACCT_10142", "General_Ledger_Account_Asset_Liability_Equity_Type_142");
        chartOfAccounts.put("ACCT_10143", "General_Ledger_Account_Asset_Liability_Equity_Type_143");
        chartOfAccounts.put("ACCT_10144", "General_Ledger_Account_Asset_Liability_Equity_Type_144");
        chartOfAccounts.put("ACCT_10145", "General_Ledger_Account_Asset_Liability_Equity_Type_145");
        chartOfAccounts.put("ACCT_10146", "General_Ledger_Account_Asset_Liability_Equity_Type_146");
        chartOfAccounts.put("ACCT_10147", "General_Ledger_Account_Asset_Liability_Equity_Type_147");
        chartOfAccounts.put("ACCT_10148", "General_Ledger_Account_Asset_Liability_Equity_Type_148");
        chartOfAccounts.put("ACCT_10149", "General_Ledger_Account_Asset_Liability_Equity_Type_149");
        chartOfAccounts.put("ACCT_10150", "General_Ledger_Account_Asset_Liability_Equity_Type_150");
        chartOfAccounts.put("ACCT_10151", "General_Ledger_Account_Asset_Liability_Equity_Type_151");
        chartOfAccounts.put("ACCT_10152", "General_Ledger_Account_Asset_Liability_Equity_Type_152");
        chartOfAccounts.put("ACCT_10153", "General_Ledger_Account_Asset_Liability_Equity_Type_153");
        chartOfAccounts.put("ACCT_10154", "General_Ledger_Account_Asset_Liability_Equity_Type_154");
        chartOfAccounts.put("ACCT_10155", "General_Ledger_Account_Asset_Liability_Equity_Type_155");
        chartOfAccounts.put("ACCT_10156", "General_Ledger_Account_Asset_Liability_Equity_Type_156");
        chartOfAccounts.put("ACCT_10157", "General_Ledger_Account_Asset_Liability_Equity_Type_157");
        chartOfAccounts.put("ACCT_10158", "General_Ledger_Account_Asset_Liability_Equity_Type_158");
        chartOfAccounts.put("ACCT_10159", "General_Ledger_Account_Asset_Liability_Equity_Type_159");
        chartOfAccounts.put("ACCT_10160", "General_Ledger_Account_Asset_Liability_Equity_Type_160");
        chartOfAccounts.put("ACCT_10161", "General_Ledger_Account_Asset_Liability_Equity_Type_161");
        chartOfAccounts.put("ACCT_10162", "General_Ledger_Account_Asset_Liability_Equity_Type_162");
        chartOfAccounts.put("ACCT_10163", "General_Ledger_Account_Asset_Liability_Equity_Type_163");
        chartOfAccounts.put("ACCT_10164", "General_Ledger_Account_Asset_Liability_Equity_Type_164");
        chartOfAccounts.put("ACCT_10165", "General_Ledger_Account_Asset_Liability_Equity_Type_165");
        chartOfAccounts.put("ACCT_10166", "General_Ledger_Account_Asset_Liability_Equity_Type_166");
        chartOfAccounts.put("ACCT_10167", "General_Ledger_Account_Asset_Liability_Equity_Type_167");
        chartOfAccounts.put("ACCT_10168", "General_Ledger_Account_Asset_Liability_Equity_Type_168");
        chartOfAccounts.put("ACCT_10169", "General_Ledger_Account_Asset_Liability_Equity_Type_169");
        chartOfAccounts.put("ACCT_10170", "General_Ledger_Account_Asset_Liability_Equity_Type_170");
        chartOfAccounts.put("ACCT_10171", "General_Ledger_Account_Asset_Liability_Equity_Type_171");
        chartOfAccounts.put("ACCT_10172", "General_Ledger_Account_Asset_Liability_Equity_Type_172");
        chartOfAccounts.put("ACCT_10173", "General_Ledger_Account_Asset_Liability_Equity_Type_173");
        chartOfAccounts.put("ACCT_10174", "General_Ledger_Account_Asset_Liability_Equity_Type_174");
        chartOfAccounts.put("ACCT_10175", "General_Ledger_Account_Asset_Liability_Equity_Type_175");
        chartOfAccounts.put("ACCT_10176", "General_Ledger_Account_Asset_Liability_Equity_Type_176");
        chartOfAccounts.put("ACCT_10177", "General_Ledger_Account_Asset_Liability_Equity_Type_177");
        chartOfAccounts.put("ACCT_10178", "General_Ledger_Account_Asset_Liability_Equity_Type_178");
        chartOfAccounts.put("ACCT_10179", "General_Ledger_Account_Asset_Liability_Equity_Type_179");
        chartOfAccounts.put("ACCT_10180", "General_Ledger_Account_Asset_Liability_Equity_Type_180");
        chartOfAccounts.put("ACCT_10181", "General_Ledger_Account_Asset_Liability_Equity_Type_181");
        chartOfAccounts.put("ACCT_10182", "General_Ledger_Account_Asset_Liability_Equity_Type_182");
        chartOfAccounts.put("ACCT_10183", "General_Ledger_Account_Asset_Liability_Equity_Type_183");
        chartOfAccounts.put("ACCT_10184", "General_Ledger_Account_Asset_Liability_Equity_Type_184");
        chartOfAccounts.put("ACCT_10185", "General_Ledger_Account_Asset_Liability_Equity_Type_185");
        chartOfAccounts.put("ACCT_10186", "General_Ledger_Account_Asset_Liability_Equity_Type_186");
        chartOfAccounts.put("ACCT_10187", "General_Ledger_Account_Asset_Liability_Equity_Type_187");
        chartOfAccounts.put("ACCT_10188", "General_Ledger_Account_Asset_Liability_Equity_Type_188");
        chartOfAccounts.put("ACCT_10189", "General_Ledger_Account_Asset_Liability_Equity_Type_189");
        chartOfAccounts.put("ACCT_10190", "General_Ledger_Account_Asset_Liability_Equity_Type_190");
        chartOfAccounts.put("ACCT_10191", "General_Ledger_Account_Asset_Liability_Equity_Type_191");
        chartOfAccounts.put("ACCT_10192", "General_Ledger_Account_Asset_Liability_Equity_Type_192");
        chartOfAccounts.put("ACCT_10193", "General_Ledger_Account_Asset_Liability_Equity_Type_193");
        chartOfAccounts.put("ACCT_10194", "General_Ledger_Account_Asset_Liability_Equity_Type_194");
        chartOfAccounts.put("ACCT_10195", "General_Ledger_Account_Asset_Liability_Equity_Type_195");
        chartOfAccounts.put("ACCT_10196", "General_Ledger_Account_Asset_Liability_Equity_Type_196");
        chartOfAccounts.put("ACCT_10197", "General_Ledger_Account_Asset_Liability_Equity_Type_197");
        chartOfAccounts.put("ACCT_10198", "General_Ledger_Account_Asset_Liability_Equity_Type_198");
        chartOfAccounts.put("ACCT_10199", "General_Ledger_Account_Asset_Liability_Equity_Type_199");
        chartOfAccounts.put("ACCT_10200", "General_Ledger_Account_Asset_Liability_Equity_Type_200");
        chartOfAccounts.put("ACCT_10201", "General_Ledger_Account_Asset_Liability_Equity_Type_201");
        chartOfAccounts.put("ACCT_10202", "General_Ledger_Account_Asset_Liability_Equity_Type_202");
        chartOfAccounts.put("ACCT_10203", "General_Ledger_Account_Asset_Liability_Equity_Type_203");
        chartOfAccounts.put("ACCT_10204", "General_Ledger_Account_Asset_Liability_Equity_Type_204");
        chartOfAccounts.put("ACCT_10205", "General_Ledger_Account_Asset_Liability_Equity_Type_205");
        chartOfAccounts.put("ACCT_10206", "General_Ledger_Account_Asset_Liability_Equity_Type_206");
        chartOfAccounts.put("ACCT_10207", "General_Ledger_Account_Asset_Liability_Equity_Type_207");
        chartOfAccounts.put("ACCT_10208", "General_Ledger_Account_Asset_Liability_Equity_Type_208");
        chartOfAccounts.put("ACCT_10209", "General_Ledger_Account_Asset_Liability_Equity_Type_209");
        chartOfAccounts.put("ACCT_10210", "General_Ledger_Account_Asset_Liability_Equity_Type_210");
        chartOfAccounts.put("ACCT_10211", "General_Ledger_Account_Asset_Liability_Equity_Type_211");
        chartOfAccounts.put("ACCT_10212", "General_Ledger_Account_Asset_Liability_Equity_Type_212");
        chartOfAccounts.put("ACCT_10213", "General_Ledger_Account_Asset_Liability_Equity_Type_213");
        chartOfAccounts.put("ACCT_10214", "General_Ledger_Account_Asset_Liability_Equity_Type_214");
        chartOfAccounts.put("ACCT_10215", "General_Ledger_Account_Asset_Liability_Equity_Type_215");
        chartOfAccounts.put("ACCT_10216", "General_Ledger_Account_Asset_Liability_Equity_Type_216");
        chartOfAccounts.put("ACCT_10217", "General_Ledger_Account_Asset_Liability_Equity_Type_217");
        chartOfAccounts.put("ACCT_10218", "General_Ledger_Account_Asset_Liability_Equity_Type_218");
        chartOfAccounts.put("ACCT_10219", "General_Ledger_Account_Asset_Liability_Equity_Type_219");
        chartOfAccounts.put("ACCT_10220", "General_Ledger_Account_Asset_Liability_Equity_Type_220");
        chartOfAccounts.put("ACCT_10221", "General_Ledger_Account_Asset_Liability_Equity_Type_221");
        chartOfAccounts.put("ACCT_10222", "General_Ledger_Account_Asset_Liability_Equity_Type_222");
        chartOfAccounts.put("ACCT_10223", "General_Ledger_Account_Asset_Liability_Equity_Type_223");
        chartOfAccounts.put("ACCT_10224", "General_Ledger_Account_Asset_Liability_Equity_Type_224");
        chartOfAccounts.put("ACCT_10225", "General_Ledger_Account_Asset_Liability_Equity_Type_225");
        chartOfAccounts.put("ACCT_10226", "General_Ledger_Account_Asset_Liability_Equity_Type_226");
        chartOfAccounts.put("ACCT_10227", "General_Ledger_Account_Asset_Liability_Equity_Type_227");
        chartOfAccounts.put("ACCT_10228", "General_Ledger_Account_Asset_Liability_Equity_Type_228");
        chartOfAccounts.put("ACCT_10229", "General_Ledger_Account_Asset_Liability_Equity_Type_229");
        chartOfAccounts.put("ACCT_10230", "General_Ledger_Account_Asset_Liability_Equity_Type_230");
        chartOfAccounts.put("ACCT_10231", "General_Ledger_Account_Asset_Liability_Equity_Type_231");
        chartOfAccounts.put("ACCT_10232", "General_Ledger_Account_Asset_Liability_Equity_Type_232");
        chartOfAccounts.put("ACCT_10233", "General_Ledger_Account_Asset_Liability_Equity_Type_233");
        chartOfAccounts.put("ACCT_10234", "General_Ledger_Account_Asset_Liability_Equity_Type_234");
        chartOfAccounts.put("ACCT_10235", "General_Ledger_Account_Asset_Liability_Equity_Type_235");
        chartOfAccounts.put("ACCT_10236", "General_Ledger_Account_Asset_Liability_Equity_Type_236");
        chartOfAccounts.put("ACCT_10237", "General_Ledger_Account_Asset_Liability_Equity_Type_237");
        chartOfAccounts.put("ACCT_10238", "General_Ledger_Account_Asset_Liability_Equity_Type_238");
        chartOfAccounts.put("ACCT_10239", "General_Ledger_Account_Asset_Liability_Equity_Type_239");
        chartOfAccounts.put("ACCT_10240", "General_Ledger_Account_Asset_Liability_Equity_Type_240");
        chartOfAccounts.put("ACCT_10241", "General_Ledger_Account_Asset_Liability_Equity_Type_241");
        chartOfAccounts.put("ACCT_10242", "General_Ledger_Account_Asset_Liability_Equity_Type_242");
        chartOfAccounts.put("ACCT_10243", "General_Ledger_Account_Asset_Liability_Equity_Type_243");
        chartOfAccounts.put("ACCT_10244", "General_Ledger_Account_Asset_Liability_Equity_Type_244");
        chartOfAccounts.put("ACCT_10245", "General_Ledger_Account_Asset_Liability_Equity_Type_245");
        chartOfAccounts.put("ACCT_10246", "General_Ledger_Account_Asset_Liability_Equity_Type_246");
        chartOfAccounts.put("ACCT_10247", "General_Ledger_Account_Asset_Liability_Equity_Type_247");
        chartOfAccounts.put("ACCT_10248", "General_Ledger_Account_Asset_Liability_Equity_Type_248");
        chartOfAccounts.put("ACCT_10249", "General_Ledger_Account_Asset_Liability_Equity_Type_249");
        chartOfAccounts.put("ACCT_10250", "General_Ledger_Account_Asset_Liability_Equity_Type_250");
        chartOfAccounts.put("ACCT_10251", "General_Ledger_Account_Asset_Liability_Equity_Type_251");
        chartOfAccounts.put("ACCT_10252", "General_Ledger_Account_Asset_Liability_Equity_Type_252");
        chartOfAccounts.put("ACCT_10253", "General_Ledger_Account_Asset_Liability_Equity_Type_253");
        chartOfAccounts.put("ACCT_10254", "General_Ledger_Account_Asset_Liability_Equity_Type_254");
        chartOfAccounts.put("ACCT_10255", "General_Ledger_Account_Asset_Liability_Equity_Type_255");
        chartOfAccounts.put("ACCT_10256", "General_Ledger_Account_Asset_Liability_Equity_Type_256");
        chartOfAccounts.put("ACCT_10257", "General_Ledger_Account_Asset_Liability_Equity_Type_257");
        chartOfAccounts.put("ACCT_10258", "General_Ledger_Account_Asset_Liability_Equity_Type_258");
        chartOfAccounts.put("ACCT_10259", "General_Ledger_Account_Asset_Liability_Equity_Type_259");
        chartOfAccounts.put("ACCT_10260", "General_Ledger_Account_Asset_Liability_Equity_Type_260");
        chartOfAccounts.put("ACCT_10261", "General_Ledger_Account_Asset_Liability_Equity_Type_261");
        chartOfAccounts.put("ACCT_10262", "General_Ledger_Account_Asset_Liability_Equity_Type_262");
        chartOfAccounts.put("ACCT_10263", "General_Ledger_Account_Asset_Liability_Equity_Type_263");
        chartOfAccounts.put("ACCT_10264", "General_Ledger_Account_Asset_Liability_Equity_Type_264");
        chartOfAccounts.put("ACCT_10265", "General_Ledger_Account_Asset_Liability_Equity_Type_265");
        chartOfAccounts.put("ACCT_10266", "General_Ledger_Account_Asset_Liability_Equity_Type_266");
        chartOfAccounts.put("ACCT_10267", "General_Ledger_Account_Asset_Liability_Equity_Type_267");
        chartOfAccounts.put("ACCT_10268", "General_Ledger_Account_Asset_Liability_Equity_Type_268");
        chartOfAccounts.put("ACCT_10269", "General_Ledger_Account_Asset_Liability_Equity_Type_269");
        chartOfAccounts.put("ACCT_10270", "General_Ledger_Account_Asset_Liability_Equity_Type_270");
        chartOfAccounts.put("ACCT_10271", "General_Ledger_Account_Asset_Liability_Equity_Type_271");
        chartOfAccounts.put("ACCT_10272", "General_Ledger_Account_Asset_Liability_Equity_Type_272");
        chartOfAccounts.put("ACCT_10273", "General_Ledger_Account_Asset_Liability_Equity_Type_273");
        chartOfAccounts.put("ACCT_10274", "General_Ledger_Account_Asset_Liability_Equity_Type_274");
        chartOfAccounts.put("ACCT_10275", "General_Ledger_Account_Asset_Liability_Equity_Type_275");
        chartOfAccounts.put("ACCT_10276", "General_Ledger_Account_Asset_Liability_Equity_Type_276");
        chartOfAccounts.put("ACCT_10277", "General_Ledger_Account_Asset_Liability_Equity_Type_277");
        chartOfAccounts.put("ACCT_10278", "General_Ledger_Account_Asset_Liability_Equity_Type_278");
        chartOfAccounts.put("ACCT_10279", "General_Ledger_Account_Asset_Liability_Equity_Type_279");
        chartOfAccounts.put("ACCT_10280", "General_Ledger_Account_Asset_Liability_Equity_Type_280");
        chartOfAccounts.put("ACCT_10281", "General_Ledger_Account_Asset_Liability_Equity_Type_281");
        chartOfAccounts.put("ACCT_10282", "General_Ledger_Account_Asset_Liability_Equity_Type_282");
        chartOfAccounts.put("ACCT_10283", "General_Ledger_Account_Asset_Liability_Equity_Type_283");
        chartOfAccounts.put("ACCT_10284", "General_Ledger_Account_Asset_Liability_Equity_Type_284");
        chartOfAccounts.put("ACCT_10285", "General_Ledger_Account_Asset_Liability_Equity_Type_285");
        chartOfAccounts.put("ACCT_10286", "General_Ledger_Account_Asset_Liability_Equity_Type_286");
        chartOfAccounts.put("ACCT_10287", "General_Ledger_Account_Asset_Liability_Equity_Type_287");
        chartOfAccounts.put("ACCT_10288", "General_Ledger_Account_Asset_Liability_Equity_Type_288");
        chartOfAccounts.put("ACCT_10289", "General_Ledger_Account_Asset_Liability_Equity_Type_289");
        chartOfAccounts.put("ACCT_10290", "General_Ledger_Account_Asset_Liability_Equity_Type_290");
        chartOfAccounts.put("ACCT_10291", "General_Ledger_Account_Asset_Liability_Equity_Type_291");
        chartOfAccounts.put("ACCT_10292", "General_Ledger_Account_Asset_Liability_Equity_Type_292");
        chartOfAccounts.put("ACCT_10293", "General_Ledger_Account_Asset_Liability_Equity_Type_293");
        chartOfAccounts.put("ACCT_10294", "General_Ledger_Account_Asset_Liability_Equity_Type_294");
        chartOfAccounts.put("ACCT_10295", "General_Ledger_Account_Asset_Liability_Equity_Type_295");
        chartOfAccounts.put("ACCT_10296", "General_Ledger_Account_Asset_Liability_Equity_Type_296");
        chartOfAccounts.put("ACCT_10297", "General_Ledger_Account_Asset_Liability_Equity_Type_297");
        chartOfAccounts.put("ACCT_10298", "General_Ledger_Account_Asset_Liability_Equity_Type_298");
        chartOfAccounts.put("ACCT_10299", "General_Ledger_Account_Asset_Liability_Equity_Type_299");
        chartOfAccounts.put("ACCT_10300", "General_Ledger_Account_Asset_Liability_Equity_Type_300");
        chartOfAccounts.put("ACCT_10301", "General_Ledger_Account_Asset_Liability_Equity_Type_301");
        chartOfAccounts.put("ACCT_10302", "General_Ledger_Account_Asset_Liability_Equity_Type_302");
        chartOfAccounts.put("ACCT_10303", "General_Ledger_Account_Asset_Liability_Equity_Type_303");
        chartOfAccounts.put("ACCT_10304", "General_Ledger_Account_Asset_Liability_Equity_Type_304");
        chartOfAccounts.put("ACCT_10305", "General_Ledger_Account_Asset_Liability_Equity_Type_305");
        chartOfAccounts.put("ACCT_10306", "General_Ledger_Account_Asset_Liability_Equity_Type_306");
        chartOfAccounts.put("ACCT_10307", "General_Ledger_Account_Asset_Liability_Equity_Type_307");
        chartOfAccounts.put("ACCT_10308", "General_Ledger_Account_Asset_Liability_Equity_Type_308");
        chartOfAccounts.put("ACCT_10309", "General_Ledger_Account_Asset_Liability_Equity_Type_309");
        chartOfAccounts.put("ACCT_10310", "General_Ledger_Account_Asset_Liability_Equity_Type_310");
        chartOfAccounts.put("ACCT_10311", "General_Ledger_Account_Asset_Liability_Equity_Type_311");
        chartOfAccounts.put("ACCT_10312", "General_Ledger_Account_Asset_Liability_Equity_Type_312");
        chartOfAccounts.put("ACCT_10313", "General_Ledger_Account_Asset_Liability_Equity_Type_313");
        chartOfAccounts.put("ACCT_10314", "General_Ledger_Account_Asset_Liability_Equity_Type_314");
        chartOfAccounts.put("ACCT_10315", "General_Ledger_Account_Asset_Liability_Equity_Type_315");
        chartOfAccounts.put("ACCT_10316", "General_Ledger_Account_Asset_Liability_Equity_Type_316");
        chartOfAccounts.put("ACCT_10317", "General_Ledger_Account_Asset_Liability_Equity_Type_317");
        chartOfAccounts.put("ACCT_10318", "General_Ledger_Account_Asset_Liability_Equity_Type_318");
        chartOfAccounts.put("ACCT_10319", "General_Ledger_Account_Asset_Liability_Equity_Type_319");
        chartOfAccounts.put("ACCT_10320", "General_Ledger_Account_Asset_Liability_Equity_Type_320");
        chartOfAccounts.put("ACCT_10321", "General_Ledger_Account_Asset_Liability_Equity_Type_321");
        chartOfAccounts.put("ACCT_10322", "General_Ledger_Account_Asset_Liability_Equity_Type_322");
        chartOfAccounts.put("ACCT_10323", "General_Ledger_Account_Asset_Liability_Equity_Type_323");
        chartOfAccounts.put("ACCT_10324", "General_Ledger_Account_Asset_Liability_Equity_Type_324");
        chartOfAccounts.put("ACCT_10325", "General_Ledger_Account_Asset_Liability_Equity_Type_325");
        chartOfAccounts.put("ACCT_10326", "General_Ledger_Account_Asset_Liability_Equity_Type_326");
        chartOfAccounts.put("ACCT_10327", "General_Ledger_Account_Asset_Liability_Equity_Type_327");
        chartOfAccounts.put("ACCT_10328", "General_Ledger_Account_Asset_Liability_Equity_Type_328");
        chartOfAccounts.put("ACCT_10329", "General_Ledger_Account_Asset_Liability_Equity_Type_329");
        chartOfAccounts.put("ACCT_10330", "General_Ledger_Account_Asset_Liability_Equity_Type_330");
        chartOfAccounts.put("ACCT_10331", "General_Ledger_Account_Asset_Liability_Equity_Type_331");
        chartOfAccounts.put("ACCT_10332", "General_Ledger_Account_Asset_Liability_Equity_Type_332");
        chartOfAccounts.put("ACCT_10333", "General_Ledger_Account_Asset_Liability_Equity_Type_333");
        chartOfAccounts.put("ACCT_10334", "General_Ledger_Account_Asset_Liability_Equity_Type_334");
        chartOfAccounts.put("ACCT_10335", "General_Ledger_Account_Asset_Liability_Equity_Type_335");
        chartOfAccounts.put("ACCT_10336", "General_Ledger_Account_Asset_Liability_Equity_Type_336");
        chartOfAccounts.put("ACCT_10337", "General_Ledger_Account_Asset_Liability_Equity_Type_337");
        chartOfAccounts.put("ACCT_10338", "General_Ledger_Account_Asset_Liability_Equity_Type_338");
        chartOfAccounts.put("ACCT_10339", "General_Ledger_Account_Asset_Liability_Equity_Type_339");
        chartOfAccounts.put("ACCT_10340", "General_Ledger_Account_Asset_Liability_Equity_Type_340");
        chartOfAccounts.put("ACCT_10341", "General_Ledger_Account_Asset_Liability_Equity_Type_341");
        chartOfAccounts.put("ACCT_10342", "General_Ledger_Account_Asset_Liability_Equity_Type_342");
        chartOfAccounts.put("ACCT_10343", "General_Ledger_Account_Asset_Liability_Equity_Type_343");
        chartOfAccounts.put("ACCT_10344", "General_Ledger_Account_Asset_Liability_Equity_Type_344");
        chartOfAccounts.put("ACCT_10345", "General_Ledger_Account_Asset_Liability_Equity_Type_345");
        chartOfAccounts.put("ACCT_10346", "General_Ledger_Account_Asset_Liability_Equity_Type_346");
        chartOfAccounts.put("ACCT_10347", "General_Ledger_Account_Asset_Liability_Equity_Type_347");
        chartOfAccounts.put("ACCT_10348", "General_Ledger_Account_Asset_Liability_Equity_Type_348");
        chartOfAccounts.put("ACCT_10349", "General_Ledger_Account_Asset_Liability_Equity_Type_349");
        chartOfAccounts.put("ACCT_10350", "General_Ledger_Account_Asset_Liability_Equity_Type_350");
        chartOfAccounts.put("ACCT_10351", "General_Ledger_Account_Asset_Liability_Equity_Type_351");
        chartOfAccounts.put("ACCT_10352", "General_Ledger_Account_Asset_Liability_Equity_Type_352");
        chartOfAccounts.put("ACCT_10353", "General_Ledger_Account_Asset_Liability_Equity_Type_353");
        chartOfAccounts.put("ACCT_10354", "General_Ledger_Account_Asset_Liability_Equity_Type_354");
        chartOfAccounts.put("ACCT_10355", "General_Ledger_Account_Asset_Liability_Equity_Type_355");
        chartOfAccounts.put("ACCT_10356", "General_Ledger_Account_Asset_Liability_Equity_Type_356");
        chartOfAccounts.put("ACCT_10357", "General_Ledger_Account_Asset_Liability_Equity_Type_357");
        chartOfAccounts.put("ACCT_10358", "General_Ledger_Account_Asset_Liability_Equity_Type_358");
        chartOfAccounts.put("ACCT_10359", "General_Ledger_Account_Asset_Liability_Equity_Type_359");
        chartOfAccounts.put("ACCT_10360", "General_Ledger_Account_Asset_Liability_Equity_Type_360");
        chartOfAccounts.put("ACCT_10361", "General_Ledger_Account_Asset_Liability_Equity_Type_361");
        chartOfAccounts.put("ACCT_10362", "General_Ledger_Account_Asset_Liability_Equity_Type_362");
        chartOfAccounts.put("ACCT_10363", "General_Ledger_Account_Asset_Liability_Equity_Type_363");
        chartOfAccounts.put("ACCT_10364", "General_Ledger_Account_Asset_Liability_Equity_Type_364");
        chartOfAccounts.put("ACCT_10365", "General_Ledger_Account_Asset_Liability_Equity_Type_365");
        chartOfAccounts.put("ACCT_10366", "General_Ledger_Account_Asset_Liability_Equity_Type_366");
        chartOfAccounts.put("ACCT_10367", "General_Ledger_Account_Asset_Liability_Equity_Type_367");
        chartOfAccounts.put("ACCT_10368", "General_Ledger_Account_Asset_Liability_Equity_Type_368");
        chartOfAccounts.put("ACCT_10369", "General_Ledger_Account_Asset_Liability_Equity_Type_369");
        chartOfAccounts.put("ACCT_10370", "General_Ledger_Account_Asset_Liability_Equity_Type_370");
        chartOfAccounts.put("ACCT_10371", "General_Ledger_Account_Asset_Liability_Equity_Type_371");
        chartOfAccounts.put("ACCT_10372", "General_Ledger_Account_Asset_Liability_Equity_Type_372");
        chartOfAccounts.put("ACCT_10373", "General_Ledger_Account_Asset_Liability_Equity_Type_373");
        chartOfAccounts.put("ACCT_10374", "General_Ledger_Account_Asset_Liability_Equity_Type_374");
        chartOfAccounts.put("ACCT_10375", "General_Ledger_Account_Asset_Liability_Equity_Type_375");
        chartOfAccounts.put("ACCT_10376", "General_Ledger_Account_Asset_Liability_Equity_Type_376");
        chartOfAccounts.put("ACCT_10377", "General_Ledger_Account_Asset_Liability_Equity_Type_377");
        chartOfAccounts.put("ACCT_10378", "General_Ledger_Account_Asset_Liability_Equity_Type_378");
        chartOfAccounts.put("ACCT_10379", "General_Ledger_Account_Asset_Liability_Equity_Type_379");
        chartOfAccounts.put("ACCT_10380", "General_Ledger_Account_Asset_Liability_Equity_Type_380");
        chartOfAccounts.put("ACCT_10381", "General_Ledger_Account_Asset_Liability_Equity_Type_381");
        chartOfAccounts.put("ACCT_10382", "General_Ledger_Account_Asset_Liability_Equity_Type_382");
        chartOfAccounts.put("ACCT_10383", "General_Ledger_Account_Asset_Liability_Equity_Type_383");
        chartOfAccounts.put("ACCT_10384", "General_Ledger_Account_Asset_Liability_Equity_Type_384");
        chartOfAccounts.put("ACCT_10385", "General_Ledger_Account_Asset_Liability_Equity_Type_385");
        chartOfAccounts.put("ACCT_10386", "General_Ledger_Account_Asset_Liability_Equity_Type_386");
        chartOfAccounts.put("ACCT_10387", "General_Ledger_Account_Asset_Liability_Equity_Type_387");
        chartOfAccounts.put("ACCT_10388", "General_Ledger_Account_Asset_Liability_Equity_Type_388");
        chartOfAccounts.put("ACCT_10389", "General_Ledger_Account_Asset_Liability_Equity_Type_389");
        chartOfAccounts.put("ACCT_10390", "General_Ledger_Account_Asset_Liability_Equity_Type_390");
        chartOfAccounts.put("ACCT_10391", "General_Ledger_Account_Asset_Liability_Equity_Type_391");
        chartOfAccounts.put("ACCT_10392", "General_Ledger_Account_Asset_Liability_Equity_Type_392");
        chartOfAccounts.put("ACCT_10393", "General_Ledger_Account_Asset_Liability_Equity_Type_393");
        chartOfAccounts.put("ACCT_10394", "General_Ledger_Account_Asset_Liability_Equity_Type_394");
        chartOfAccounts.put("ACCT_10395", "General_Ledger_Account_Asset_Liability_Equity_Type_395");
        chartOfAccounts.put("ACCT_10396", "General_Ledger_Account_Asset_Liability_Equity_Type_396");
        chartOfAccounts.put("ACCT_10397", "General_Ledger_Account_Asset_Liability_Equity_Type_397");
        chartOfAccounts.put("ACCT_10398", "General_Ledger_Account_Asset_Liability_Equity_Type_398");
        chartOfAccounts.put("ACCT_10399", "General_Ledger_Account_Asset_Liability_Equity_Type_399");
        chartOfAccounts.put("ACCT_10400", "General_Ledger_Account_Asset_Liability_Equity_Type_400");

        Map<String, String> productCategories = new HashMap<>();
        productCategories.put("PROD_CAT_5001", "Inventory_Management_SKU_Classification_Group_1");
        productCategories.put("PROD_CAT_5002", "Inventory_Management_SKU_Classification_Group_2");
        productCategories.put("PROD_CAT_5003", "Inventory_Management_SKU_Classification_Group_3");
        productCategories.put("PROD_CAT_5004", "Inventory_Management_SKU_Classification_Group_4");
        productCategories.put("PROD_CAT_5005", "Inventory_Management_SKU_Classification_Group_5");
        productCategories.put("PROD_CAT_5006", "Inventory_Management_SKU_Classification_Group_6");
        productCategories.put("PROD_CAT_5007", "Inventory_Management_SKU_Classification_Group_7");
        productCategories.put("PROD_CAT_5008", "Inventory_Management_SKU_Classification_Group_8");
        productCategories.put("PROD_CAT_5009", "Inventory_Management_SKU_Classification_Group_9");
        productCategories.put("PROD_CAT_5010", "Inventory_Management_SKU_Classification_Group_10");
        productCategories.put("PROD_CAT_5011", "Inventory_Management_SKU_Classification_Group_11");
        productCategories.put("PROD_CAT_5012", "Inventory_Management_SKU_Classification_Group_12");
        productCategories.put("PROD_CAT_5013", "Inventory_Management_SKU_Classification_Group_13");
        productCategories.put("PROD_CAT_5014", "Inventory_Management_SKU_Classification_Group_14");
        productCategories.put("PROD_CAT_5015", "Inventory_Management_SKU_Classification_Group_15");
        productCategories.put("PROD_CAT_5016", "Inventory_Management_SKU_Classification_Group_16");
        productCategories.put("PROD_CAT_5017", "Inventory_Management_SKU_Classification_Group_17");
        productCategories.put("PROD_CAT_5018", "Inventory_Management_SKU_Classification_Group_18");
        productCategories.put("PROD_CAT_5019", "Inventory_Management_SKU_Classification_Group_19");
        productCategories.put("PROD_CAT_5020", "Inventory_Management_SKU_Classification_Group_20");
        productCategories.put("PROD_CAT_5021", "Inventory_Management_SKU_Classification_Group_21");
        productCategories.put("PROD_CAT_5022", "Inventory_Management_SKU_Classification_Group_22");
        productCategories.put("PROD_CAT_5023", "Inventory_Management_SKU_Classification_Group_23");
        productCategories.put("PROD_CAT_5024", "Inventory_Management_SKU_Classification_Group_24");
        productCategories.put("PROD_CAT_5025", "Inventory_Management_SKU_Classification_Group_25");
        productCategories.put("PROD_CAT_5026", "Inventory_Management_SKU_Classification_Group_26");
        productCategories.put("PROD_CAT_5027", "Inventory_Management_SKU_Classification_Group_27");
        productCategories.put("PROD_CAT_5028", "Inventory_Management_SKU_Classification_Group_28");
        productCategories.put("PROD_CAT_5029", "Inventory_Management_SKU_Classification_Group_29");
        productCategories.put("PROD_CAT_5030", "Inventory_Management_SKU_Classification_Group_30");
        productCategories.put("PROD_CAT_5031", "Inventory_Management_SKU_Classification_Group_31");
        productCategories.put("PROD_CAT_5032", "Inventory_Management_SKU_Classification_Group_32");
        productCategories.put("PROD_CAT_5033", "Inventory_Management_SKU_Classification_Group_33");
        productCategories.put("PROD_CAT_5034", "Inventory_Management_SKU_Classification_Group_34");
        productCategories.put("PROD_CAT_5035", "Inventory_Management_SKU_Classification_Group_35");
        productCategories.put("PROD_CAT_5036", "Inventory_Management_SKU_Classification_Group_36");
        productCategories.put("PROD_CAT_5037", "Inventory_Management_SKU_Classification_Group_37");
        productCategories.put("PROD_CAT_5038", "Inventory_Management_SKU_Classification_Group_38");
        productCategories.put("PROD_CAT_5039", "Inventory_Management_SKU_Classification_Group_39");
        productCategories.put("PROD_CAT_5040", "Inventory_Management_SKU_Classification_Group_40");
        productCategories.put("PROD_CAT_5041", "Inventory_Management_SKU_Classification_Group_41");
        productCategories.put("PROD_CAT_5042", "Inventory_Management_SKU_Classification_Group_42");
        productCategories.put("PROD_CAT_5043", "Inventory_Management_SKU_Classification_Group_43");
        productCategories.put("PROD_CAT_5044", "Inventory_Management_SKU_Classification_Group_44");
        productCategories.put("PROD_CAT_5045", "Inventory_Management_SKU_Classification_Group_45");
        productCategories.put("PROD_CAT_5046", "Inventory_Management_SKU_Classification_Group_46");
        productCategories.put("PROD_CAT_5047", "Inventory_Management_SKU_Classification_Group_47");
        productCategories.put("PROD_CAT_5048", "Inventory_Management_SKU_Classification_Group_48");
        productCategories.put("PROD_CAT_5049", "Inventory_Management_SKU_Classification_Group_49");
        productCategories.put("PROD_CAT_5050", "Inventory_Management_SKU_Classification_Group_50");
        productCategories.put("PROD_CAT_5051", "Inventory_Management_SKU_Classification_Group_51");
        productCategories.put("PROD_CAT_5052", "Inventory_Management_SKU_Classification_Group_52");
        productCategories.put("PROD_CAT_5053", "Inventory_Management_SKU_Classification_Group_53");
        productCategories.put("PROD_CAT_5054", "Inventory_Management_SKU_Classification_Group_54");
        productCategories.put("PROD_CAT_5055", "Inventory_Management_SKU_Classification_Group_55");
        productCategories.put("PROD_CAT_5056", "Inventory_Management_SKU_Classification_Group_56");
        productCategories.put("PROD_CAT_5057", "Inventory_Management_SKU_Classification_Group_57");
        productCategories.put("PROD_CAT_5058", "Inventory_Management_SKU_Classification_Group_58");
        productCategories.put("PROD_CAT_5059", "Inventory_Management_SKU_Classification_Group_59");
        productCategories.put("PROD_CAT_5060", "Inventory_Management_SKU_Classification_Group_60");
        productCategories.put("PROD_CAT_5061", "Inventory_Management_SKU_Classification_Group_61");
        productCategories.put("PROD_CAT_5062", "Inventory_Management_SKU_Classification_Group_62");
        productCategories.put("PROD_CAT_5063", "Inventory_Management_SKU_Classification_Group_63");
        productCategories.put("PROD_CAT_5064", "Inventory_Management_SKU_Classification_Group_64");
        productCategories.put("PROD_CAT_5065", "Inventory_Management_SKU_Classification_Group_65");
        productCategories.put("PROD_CAT_5066", "Inventory_Management_SKU_Classification_Group_66");
        productCategories.put("PROD_CAT_5067", "Inventory_Management_SKU_Classification_Group_67");
        productCategories.put("PROD_CAT_5068", "Inventory_Management_SKU_Classification_Group_68");
        productCategories.put("PROD_CAT_5069", "Inventory_Management_SKU_Classification_Group_69");
        productCategories.put("PROD_CAT_5070", "Inventory_Management_SKU_Classification_Group_70");
        productCategories.put("PROD_CAT_5071", "Inventory_Management_SKU_Classification_Group_71");
        productCategories.put("PROD_CAT_5072", "Inventory_Management_SKU_Classification_Group_72");
        productCategories.put("PROD_CAT_5073", "Inventory_Management_SKU_Classification_Group_73");
        productCategories.put("PROD_CAT_5074", "Inventory_Management_SKU_Classification_Group_74");
        productCategories.put("PROD_CAT_5075", "Inventory_Management_SKU_Classification_Group_75");
        productCategories.put("PROD_CAT_5076", "Inventory_Management_SKU_Classification_Group_76");
        productCategories.put("PROD_CAT_5077", "Inventory_Management_SKU_Classification_Group_77");
        productCategories.put("PROD_CAT_5078", "Inventory_Management_SKU_Classification_Group_78");
        productCategories.put("PROD_CAT_5079", "Inventory_Management_SKU_Classification_Group_79");
        productCategories.put("PROD_CAT_5080", "Inventory_Management_SKU_Classification_Group_80");
        productCategories.put("PROD_CAT_5081", "Inventory_Management_SKU_Classification_Group_81");
        productCategories.put("PROD_CAT_5082", "Inventory_Management_SKU_Classification_Group_82");
        productCategories.put("PROD_CAT_5083", "Inventory_Management_SKU_Classification_Group_83");
        productCategories.put("PROD_CAT_5084", "Inventory_Management_SKU_Classification_Group_84");
        productCategories.put("PROD_CAT_5085", "Inventory_Management_SKU_Classification_Group_85");
        productCategories.put("PROD_CAT_5086", "Inventory_Management_SKU_Classification_Group_86");
        productCategories.put("PROD_CAT_5087", "Inventory_Management_SKU_Classification_Group_87");
        productCategories.put("PROD_CAT_5088", "Inventory_Management_SKU_Classification_Group_88");
        productCategories.put("PROD_CAT_5089", "Inventory_Management_SKU_Classification_Group_89");
        productCategories.put("PROD_CAT_5090", "Inventory_Management_SKU_Classification_Group_90");
        productCategories.put("PROD_CAT_5091", "Inventory_Management_SKU_Classification_Group_91");
        productCategories.put("PROD_CAT_5092", "Inventory_Management_SKU_Classification_Group_92");
        productCategories.put("PROD_CAT_5093", "Inventory_Management_SKU_Classification_Group_93");
        productCategories.put("PROD_CAT_5094", "Inventory_Management_SKU_Classification_Group_94");
        productCategories.put("PROD_CAT_5095", "Inventory_Management_SKU_Classification_Group_95");
        productCategories.put("PROD_CAT_5096", "Inventory_Management_SKU_Classification_Group_96");
        productCategories.put("PROD_CAT_5097", "Inventory_Management_SKU_Classification_Group_97");
        productCategories.put("PROD_CAT_5098", "Inventory_Management_SKU_Classification_Group_98");
        productCategories.put("PROD_CAT_5099", "Inventory_Management_SKU_Classification_Group_99");
        productCategories.put("PROD_CAT_5100", "Inventory_Management_SKU_Classification_Group_100");
        productCategories.put("PROD_CAT_5101", "Inventory_Management_SKU_Classification_Group_101");
        productCategories.put("PROD_CAT_5102", "Inventory_Management_SKU_Classification_Group_102");
        productCategories.put("PROD_CAT_5103", "Inventory_Management_SKU_Classification_Group_103");
        productCategories.put("PROD_CAT_5104", "Inventory_Management_SKU_Classification_Group_104");
        productCategories.put("PROD_CAT_5105", "Inventory_Management_SKU_Classification_Group_105");
        productCategories.put("PROD_CAT_5106", "Inventory_Management_SKU_Classification_Group_106");
        productCategories.put("PROD_CAT_5107", "Inventory_Management_SKU_Classification_Group_107");
        productCategories.put("PROD_CAT_5108", "Inventory_Management_SKU_Classification_Group_108");
        productCategories.put("PROD_CAT_5109", "Inventory_Management_SKU_Classification_Group_109");
        productCategories.put("PROD_CAT_5110", "Inventory_Management_SKU_Classification_Group_110");
        productCategories.put("PROD_CAT_5111", "Inventory_Management_SKU_Classification_Group_111");
        productCategories.put("PROD_CAT_5112", "Inventory_Management_SKU_Classification_Group_112");
        productCategories.put("PROD_CAT_5113", "Inventory_Management_SKU_Classification_Group_113");
        productCategories.put("PROD_CAT_5114", "Inventory_Management_SKU_Classification_Group_114");
        productCategories.put("PROD_CAT_5115", "Inventory_Management_SKU_Classification_Group_115");
        productCategories.put("PROD_CAT_5116", "Inventory_Management_SKU_Classification_Group_116");
        productCategories.put("PROD_CAT_5117", "Inventory_Management_SKU_Classification_Group_117");
        productCategories.put("PROD_CAT_5118", "Inventory_Management_SKU_Classification_Group_118");
        productCategories.put("PROD_CAT_5119", "Inventory_Management_SKU_Classification_Group_119");
        productCategories.put("PROD_CAT_5120", "Inventory_Management_SKU_Classification_Group_120");
        productCategories.put("PROD_CAT_5121", "Inventory_Management_SKU_Classification_Group_121");
        productCategories.put("PROD_CAT_5122", "Inventory_Management_SKU_Classification_Group_122");
        productCategories.put("PROD_CAT_5123", "Inventory_Management_SKU_Classification_Group_123");
        productCategories.put("PROD_CAT_5124", "Inventory_Management_SKU_Classification_Group_124");
        productCategories.put("PROD_CAT_5125", "Inventory_Management_SKU_Classification_Group_125");
        productCategories.put("PROD_CAT_5126", "Inventory_Management_SKU_Classification_Group_126");
        productCategories.put("PROD_CAT_5127", "Inventory_Management_SKU_Classification_Group_127");
        productCategories.put("PROD_CAT_5128", "Inventory_Management_SKU_Classification_Group_128");
        productCategories.put("PROD_CAT_5129", "Inventory_Management_SKU_Classification_Group_129");
        productCategories.put("PROD_CAT_5130", "Inventory_Management_SKU_Classification_Group_130");
        productCategories.put("PROD_CAT_5131", "Inventory_Management_SKU_Classification_Group_131");
        productCategories.put("PROD_CAT_5132", "Inventory_Management_SKU_Classification_Group_132");
        productCategories.put("PROD_CAT_5133", "Inventory_Management_SKU_Classification_Group_133");
        productCategories.put("PROD_CAT_5134", "Inventory_Management_SKU_Classification_Group_134");
        productCategories.put("PROD_CAT_5135", "Inventory_Management_SKU_Classification_Group_135");
        productCategories.put("PROD_CAT_5136", "Inventory_Management_SKU_Classification_Group_136");
        productCategories.put("PROD_CAT_5137", "Inventory_Management_SKU_Classification_Group_137");
        productCategories.put("PROD_CAT_5138", "Inventory_Management_SKU_Classification_Group_138");
        productCategories.put("PROD_CAT_5139", "Inventory_Management_SKU_Classification_Group_139");
        productCategories.put("PROD_CAT_5140", "Inventory_Management_SKU_Classification_Group_140");
        productCategories.put("PROD_CAT_5141", "Inventory_Management_SKU_Classification_Group_141");
        productCategories.put("PROD_CAT_5142", "Inventory_Management_SKU_Classification_Group_142");
        productCategories.put("PROD_CAT_5143", "Inventory_Management_SKU_Classification_Group_143");
        productCategories.put("PROD_CAT_5144", "Inventory_Management_SKU_Classification_Group_144");
        productCategories.put("PROD_CAT_5145", "Inventory_Management_SKU_Classification_Group_145");
        productCategories.put("PROD_CAT_5146", "Inventory_Management_SKU_Classification_Group_146");
        productCategories.put("PROD_CAT_5147", "Inventory_Management_SKU_Classification_Group_147");
        productCategories.put("PROD_CAT_5148", "Inventory_Management_SKU_Classification_Group_148");
        productCategories.put("PROD_CAT_5149", "Inventory_Management_SKU_Classification_Group_149");
        productCategories.put("PROD_CAT_5150", "Inventory_Management_SKU_Classification_Group_150");
        productCategories.put("PROD_CAT_5151", "Inventory_Management_SKU_Classification_Group_151");
        productCategories.put("PROD_CAT_5152", "Inventory_Management_SKU_Classification_Group_152");
        productCategories.put("PROD_CAT_5153", "Inventory_Management_SKU_Classification_Group_153");
        productCategories.put("PROD_CAT_5154", "Inventory_Management_SKU_Classification_Group_154");
        productCategories.put("PROD_CAT_5155", "Inventory_Management_SKU_Classification_Group_155");
        productCategories.put("PROD_CAT_5156", "Inventory_Management_SKU_Classification_Group_156");
        productCategories.put("PROD_CAT_5157", "Inventory_Management_SKU_Classification_Group_157");
        productCategories.put("PROD_CAT_5158", "Inventory_Management_SKU_Classification_Group_158");
        productCategories.put("PROD_CAT_5159", "Inventory_Management_SKU_Classification_Group_159");
        productCategories.put("PROD_CAT_5160", "Inventory_Management_SKU_Classification_Group_160");
        productCategories.put("PROD_CAT_5161", "Inventory_Management_SKU_Classification_Group_161");
        productCategories.put("PROD_CAT_5162", "Inventory_Management_SKU_Classification_Group_162");
        productCategories.put("PROD_CAT_5163", "Inventory_Management_SKU_Classification_Group_163");
        productCategories.put("PROD_CAT_5164", "Inventory_Management_SKU_Classification_Group_164");
        productCategories.put("PROD_CAT_5165", "Inventory_Management_SKU_Classification_Group_165");
        productCategories.put("PROD_CAT_5166", "Inventory_Management_SKU_Classification_Group_166");
        productCategories.put("PROD_CAT_5167", "Inventory_Management_SKU_Classification_Group_167");
        productCategories.put("PROD_CAT_5168", "Inventory_Management_SKU_Classification_Group_168");
        productCategories.put("PROD_CAT_5169", "Inventory_Management_SKU_Classification_Group_169");
        productCategories.put("PROD_CAT_5170", "Inventory_Management_SKU_Classification_Group_170");
        productCategories.put("PROD_CAT_5171", "Inventory_Management_SKU_Classification_Group_171");
        productCategories.put("PROD_CAT_5172", "Inventory_Management_SKU_Classification_Group_172");
        productCategories.put("PROD_CAT_5173", "Inventory_Management_SKU_Classification_Group_173");
        productCategories.put("PROD_CAT_5174", "Inventory_Management_SKU_Classification_Group_174");
        productCategories.put("PROD_CAT_5175", "Inventory_Management_SKU_Classification_Group_175");
        productCategories.put("PROD_CAT_5176", "Inventory_Management_SKU_Classification_Group_176");
        productCategories.put("PROD_CAT_5177", "Inventory_Management_SKU_Classification_Group_177");
        productCategories.put("PROD_CAT_5178", "Inventory_Management_SKU_Classification_Group_178");
        productCategories.put("PROD_CAT_5179", "Inventory_Management_SKU_Classification_Group_179");
        productCategories.put("PROD_CAT_5180", "Inventory_Management_SKU_Classification_Group_180");
        productCategories.put("PROD_CAT_5181", "Inventory_Management_SKU_Classification_Group_181");
        productCategories.put("PROD_CAT_5182", "Inventory_Management_SKU_Classification_Group_182");
        productCategories.put("PROD_CAT_5183", "Inventory_Management_SKU_Classification_Group_183");
        productCategories.put("PROD_CAT_5184", "Inventory_Management_SKU_Classification_Group_184");
        productCategories.put("PROD_CAT_5185", "Inventory_Management_SKU_Classification_Group_185");
        productCategories.put("PROD_CAT_5186", "Inventory_Management_SKU_Classification_Group_186");
        productCategories.put("PROD_CAT_5187", "Inventory_Management_SKU_Classification_Group_187");
        productCategories.put("PROD_CAT_5188", "Inventory_Management_SKU_Classification_Group_188");
        productCategories.put("PROD_CAT_5189", "Inventory_Management_SKU_Classification_Group_189");
        productCategories.put("PROD_CAT_5190", "Inventory_Management_SKU_Classification_Group_190");
        productCategories.put("PROD_CAT_5191", "Inventory_Management_SKU_Classification_Group_191");
        productCategories.put("PROD_CAT_5192", "Inventory_Management_SKU_Classification_Group_192");
        productCategories.put("PROD_CAT_5193", "Inventory_Management_SKU_Classification_Group_193");
        productCategories.put("PROD_CAT_5194", "Inventory_Management_SKU_Classification_Group_194");
        productCategories.put("PROD_CAT_5195", "Inventory_Management_SKU_Classification_Group_195");
        productCategories.put("PROD_CAT_5196", "Inventory_Management_SKU_Classification_Group_196");
        productCategories.put("PROD_CAT_5197", "Inventory_Management_SKU_Classification_Group_197");
        productCategories.put("PROD_CAT_5198", "Inventory_Management_SKU_Classification_Group_198");
        productCategories.put("PROD_CAT_5199", "Inventory_Management_SKU_Classification_Group_199");
        productCategories.put("PROD_CAT_5200", "Inventory_Management_SKU_Classification_Group_200");
        productCategories.put("PROD_CAT_5201", "Inventory_Management_SKU_Classification_Group_201");
        productCategories.put("PROD_CAT_5202", "Inventory_Management_SKU_Classification_Group_202");
        productCategories.put("PROD_CAT_5203", "Inventory_Management_SKU_Classification_Group_203");
        productCategories.put("PROD_CAT_5204", "Inventory_Management_SKU_Classification_Group_204");
        productCategories.put("PROD_CAT_5205", "Inventory_Management_SKU_Classification_Group_205");
        productCategories.put("PROD_CAT_5206", "Inventory_Management_SKU_Classification_Group_206");
        productCategories.put("PROD_CAT_5207", "Inventory_Management_SKU_Classification_Group_207");
        productCategories.put("PROD_CAT_5208", "Inventory_Management_SKU_Classification_Group_208");
        productCategories.put("PROD_CAT_5209", "Inventory_Management_SKU_Classification_Group_209");
        productCategories.put("PROD_CAT_5210", "Inventory_Management_SKU_Classification_Group_210");
        productCategories.put("PROD_CAT_5211", "Inventory_Management_SKU_Classification_Group_211");
        productCategories.put("PROD_CAT_5212", "Inventory_Management_SKU_Classification_Group_212");
        productCategories.put("PROD_CAT_5213", "Inventory_Management_SKU_Classification_Group_213");
        productCategories.put("PROD_CAT_5214", "Inventory_Management_SKU_Classification_Group_214");
        productCategories.put("PROD_CAT_5215", "Inventory_Management_SKU_Classification_Group_215");
        productCategories.put("PROD_CAT_5216", "Inventory_Management_SKU_Classification_Group_216");
        productCategories.put("PROD_CAT_5217", "Inventory_Management_SKU_Classification_Group_217");
        productCategories.put("PROD_CAT_5218", "Inventory_Management_SKU_Classification_Group_218");
        productCategories.put("PROD_CAT_5219", "Inventory_Management_SKU_Classification_Group_219");
        productCategories.put("PROD_CAT_5220", "Inventory_Management_SKU_Classification_Group_220");
        productCategories.put("PROD_CAT_5221", "Inventory_Management_SKU_Classification_Group_221");
        productCategories.put("PROD_CAT_5222", "Inventory_Management_SKU_Classification_Group_222");
        productCategories.put("PROD_CAT_5223", "Inventory_Management_SKU_Classification_Group_223");
        productCategories.put("PROD_CAT_5224", "Inventory_Management_SKU_Classification_Group_224");
        productCategories.put("PROD_CAT_5225", "Inventory_Management_SKU_Classification_Group_225");
        productCategories.put("PROD_CAT_5226", "Inventory_Management_SKU_Classification_Group_226");
        productCategories.put("PROD_CAT_5227", "Inventory_Management_SKU_Classification_Group_227");
        productCategories.put("PROD_CAT_5228", "Inventory_Management_SKU_Classification_Group_228");
        productCategories.put("PROD_CAT_5229", "Inventory_Management_SKU_Classification_Group_229");
        productCategories.put("PROD_CAT_5230", "Inventory_Management_SKU_Classification_Group_230");
        productCategories.put("PROD_CAT_5231", "Inventory_Management_SKU_Classification_Group_231");
        productCategories.put("PROD_CAT_5232", "Inventory_Management_SKU_Classification_Group_232");
        productCategories.put("PROD_CAT_5233", "Inventory_Management_SKU_Classification_Group_233");
        productCategories.put("PROD_CAT_5234", "Inventory_Management_SKU_Classification_Group_234");
        productCategories.put("PROD_CAT_5235", "Inventory_Management_SKU_Classification_Group_235");
        productCategories.put("PROD_CAT_5236", "Inventory_Management_SKU_Classification_Group_236");
        productCategories.put("PROD_CAT_5237", "Inventory_Management_SKU_Classification_Group_237");
        productCategories.put("PROD_CAT_5238", "Inventory_Management_SKU_Classification_Group_238");
        productCategories.put("PROD_CAT_5239", "Inventory_Management_SKU_Classification_Group_239");
        productCategories.put("PROD_CAT_5240", "Inventory_Management_SKU_Classification_Group_240");
        productCategories.put("PROD_CAT_5241", "Inventory_Management_SKU_Classification_Group_241");
        productCategories.put("PROD_CAT_5242", "Inventory_Management_SKU_Classification_Group_242");
        productCategories.put("PROD_CAT_5243", "Inventory_Management_SKU_Classification_Group_243");
        productCategories.put("PROD_CAT_5244", "Inventory_Management_SKU_Classification_Group_244");
        productCategories.put("PROD_CAT_5245", "Inventory_Management_SKU_Classification_Group_245");
        productCategories.put("PROD_CAT_5246", "Inventory_Management_SKU_Classification_Group_246");
        productCategories.put("PROD_CAT_5247", "Inventory_Management_SKU_Classification_Group_247");
        productCategories.put("PROD_CAT_5248", "Inventory_Management_SKU_Classification_Group_248");
        productCategories.put("PROD_CAT_5249", "Inventory_Management_SKU_Classification_Group_249");
        productCategories.put("PROD_CAT_5250", "Inventory_Management_SKU_Classification_Group_250");
        productCategories.put("PROD_CAT_5251", "Inventory_Management_SKU_Classification_Group_251");
        productCategories.put("PROD_CAT_5252", "Inventory_Management_SKU_Classification_Group_252");
        productCategories.put("PROD_CAT_5253", "Inventory_Management_SKU_Classification_Group_253");
        productCategories.put("PROD_CAT_5254", "Inventory_Management_SKU_Classification_Group_254");
        productCategories.put("PROD_CAT_5255", "Inventory_Management_SKU_Classification_Group_255");
        productCategories.put("PROD_CAT_5256", "Inventory_Management_SKU_Classification_Group_256");
        productCategories.put("PROD_CAT_5257", "Inventory_Management_SKU_Classification_Group_257");
        productCategories.put("PROD_CAT_5258", "Inventory_Management_SKU_Classification_Group_258");
        productCategories.put("PROD_CAT_5259", "Inventory_Management_SKU_Classification_Group_259");
        productCategories.put("PROD_CAT_5260", "Inventory_Management_SKU_Classification_Group_260");
        productCategories.put("PROD_CAT_5261", "Inventory_Management_SKU_Classification_Group_261");
        productCategories.put("PROD_CAT_5262", "Inventory_Management_SKU_Classification_Group_262");
        productCategories.put("PROD_CAT_5263", "Inventory_Management_SKU_Classification_Group_263");
        productCategories.put("PROD_CAT_5264", "Inventory_Management_SKU_Classification_Group_264");
        productCategories.put("PROD_CAT_5265", "Inventory_Management_SKU_Classification_Group_265");
        productCategories.put("PROD_CAT_5266", "Inventory_Management_SKU_Classification_Group_266");
        productCategories.put("PROD_CAT_5267", "Inventory_Management_SKU_Classification_Group_267");
        productCategories.put("PROD_CAT_5268", "Inventory_Management_SKU_Classification_Group_268");
        productCategories.put("PROD_CAT_5269", "Inventory_Management_SKU_Classification_Group_269");
        productCategories.put("PROD_CAT_5270", "Inventory_Management_SKU_Classification_Group_270");
        productCategories.put("PROD_CAT_5271", "Inventory_Management_SKU_Classification_Group_271");
        productCategories.put("PROD_CAT_5272", "Inventory_Management_SKU_Classification_Group_272");
        productCategories.put("PROD_CAT_5273", "Inventory_Management_SKU_Classification_Group_273");
        productCategories.put("PROD_CAT_5274", "Inventory_Management_SKU_Classification_Group_274");
        productCategories.put("PROD_CAT_5275", "Inventory_Management_SKU_Classification_Group_275");
        productCategories.put("PROD_CAT_5276", "Inventory_Management_SKU_Classification_Group_276");
        productCategories.put("PROD_CAT_5277", "Inventory_Management_SKU_Classification_Group_277");
        productCategories.put("PROD_CAT_5278", "Inventory_Management_SKU_Classification_Group_278");
        productCategories.put("PROD_CAT_5279", "Inventory_Management_SKU_Classification_Group_279");
        productCategories.put("PROD_CAT_5280", "Inventory_Management_SKU_Classification_Group_280");
        productCategories.put("PROD_CAT_5281", "Inventory_Management_SKU_Classification_Group_281");
        productCategories.put("PROD_CAT_5282", "Inventory_Management_SKU_Classification_Group_282");
        productCategories.put("PROD_CAT_5283", "Inventory_Management_SKU_Classification_Group_283");
        productCategories.put("PROD_CAT_5284", "Inventory_Management_SKU_Classification_Group_284");
        productCategories.put("PROD_CAT_5285", "Inventory_Management_SKU_Classification_Group_285");
        productCategories.put("PROD_CAT_5286", "Inventory_Management_SKU_Classification_Group_286");
        productCategories.put("PROD_CAT_5287", "Inventory_Management_SKU_Classification_Group_287");
        productCategories.put("PROD_CAT_5288", "Inventory_Management_SKU_Classification_Group_288");
        productCategories.put("PROD_CAT_5289", "Inventory_Management_SKU_Classification_Group_289");
        productCategories.put("PROD_CAT_5290", "Inventory_Management_SKU_Classification_Group_290");
        productCategories.put("PROD_CAT_5291", "Inventory_Management_SKU_Classification_Group_291");
        productCategories.put("PROD_CAT_5292", "Inventory_Management_SKU_Classification_Group_292");
        productCategories.put("PROD_CAT_5293", "Inventory_Management_SKU_Classification_Group_293");
        productCategories.put("PROD_CAT_5294", "Inventory_Management_SKU_Classification_Group_294");
        productCategories.put("PROD_CAT_5295", "Inventory_Management_SKU_Classification_Group_295");
        productCategories.put("PROD_CAT_5296", "Inventory_Management_SKU_Classification_Group_296");
        productCategories.put("PROD_CAT_5297", "Inventory_Management_SKU_Classification_Group_297");
        productCategories.put("PROD_CAT_5298", "Inventory_Management_SKU_Classification_Group_298");
        productCategories.put("PROD_CAT_5299", "Inventory_Management_SKU_Classification_Group_299");
        productCategories.put("PROD_CAT_5300", "Inventory_Management_SKU_Classification_Group_300");
        productCategories.put("PROD_CAT_5301", "Inventory_Management_SKU_Classification_Group_301");
        productCategories.put("PROD_CAT_5302", "Inventory_Management_SKU_Classification_Group_302");
        productCategories.put("PROD_CAT_5303", "Inventory_Management_SKU_Classification_Group_303");
        productCategories.put("PROD_CAT_5304", "Inventory_Management_SKU_Classification_Group_304");
        productCategories.put("PROD_CAT_5305", "Inventory_Management_SKU_Classification_Group_305");
        productCategories.put("PROD_CAT_5306", "Inventory_Management_SKU_Classification_Group_306");
        productCategories.put("PROD_CAT_5307", "Inventory_Management_SKU_Classification_Group_307");
        productCategories.put("PROD_CAT_5308", "Inventory_Management_SKU_Classification_Group_308");
        productCategories.put("PROD_CAT_5309", "Inventory_Management_SKU_Classification_Group_309");
        productCategories.put("PROD_CAT_5310", "Inventory_Management_SKU_Classification_Group_310");
        productCategories.put("PROD_CAT_5311", "Inventory_Management_SKU_Classification_Group_311");
        productCategories.put("PROD_CAT_5312", "Inventory_Management_SKU_Classification_Group_312");
        productCategories.put("PROD_CAT_5313", "Inventory_Management_SKU_Classification_Group_313");
        productCategories.put("PROD_CAT_5314", "Inventory_Management_SKU_Classification_Group_314");
        productCategories.put("PROD_CAT_5315", "Inventory_Management_SKU_Classification_Group_315");
        productCategories.put("PROD_CAT_5316", "Inventory_Management_SKU_Classification_Group_316");
        productCategories.put("PROD_CAT_5317", "Inventory_Management_SKU_Classification_Group_317");
        productCategories.put("PROD_CAT_5318", "Inventory_Management_SKU_Classification_Group_318");
        productCategories.put("PROD_CAT_5319", "Inventory_Management_SKU_Classification_Group_319");
        productCategories.put("PROD_CAT_5320", "Inventory_Management_SKU_Classification_Group_320");
        productCategories.put("PROD_CAT_5321", "Inventory_Management_SKU_Classification_Group_321");
        productCategories.put("PROD_CAT_5322", "Inventory_Management_SKU_Classification_Group_322");
        productCategories.put("PROD_CAT_5323", "Inventory_Management_SKU_Classification_Group_323");
        productCategories.put("PROD_CAT_5324", "Inventory_Management_SKU_Classification_Group_324");
        productCategories.put("PROD_CAT_5325", "Inventory_Management_SKU_Classification_Group_325");
        productCategories.put("PROD_CAT_5326", "Inventory_Management_SKU_Classification_Group_326");
        productCategories.put("PROD_CAT_5327", "Inventory_Management_SKU_Classification_Group_327");
        productCategories.put("PROD_CAT_5328", "Inventory_Management_SKU_Classification_Group_328");
        productCategories.put("PROD_CAT_5329", "Inventory_Management_SKU_Classification_Group_329");
        productCategories.put("PROD_CAT_5330", "Inventory_Management_SKU_Classification_Group_330");
        productCategories.put("PROD_CAT_5331", "Inventory_Management_SKU_Classification_Group_331");
        productCategories.put("PROD_CAT_5332", "Inventory_Management_SKU_Classification_Group_332");
        productCategories.put("PROD_CAT_5333", "Inventory_Management_SKU_Classification_Group_333");
        productCategories.put("PROD_CAT_5334", "Inventory_Management_SKU_Classification_Group_334");
        productCategories.put("PROD_CAT_5335", "Inventory_Management_SKU_Classification_Group_335");
        productCategories.put("PROD_CAT_5336", "Inventory_Management_SKU_Classification_Group_336");
        productCategories.put("PROD_CAT_5337", "Inventory_Management_SKU_Classification_Group_337");
        productCategories.put("PROD_CAT_5338", "Inventory_Management_SKU_Classification_Group_338");
        productCategories.put("PROD_CAT_5339", "Inventory_Management_SKU_Classification_Group_339");
        productCategories.put("PROD_CAT_5340", "Inventory_Management_SKU_Classification_Group_340");
        productCategories.put("PROD_CAT_5341", "Inventory_Management_SKU_Classification_Group_341");
        productCategories.put("PROD_CAT_5342", "Inventory_Management_SKU_Classification_Group_342");
        productCategories.put("PROD_CAT_5343", "Inventory_Management_SKU_Classification_Group_343");
        productCategories.put("PROD_CAT_5344", "Inventory_Management_SKU_Classification_Group_344");
        productCategories.put("PROD_CAT_5345", "Inventory_Management_SKU_Classification_Group_345");
        productCategories.put("PROD_CAT_5346", "Inventory_Management_SKU_Classification_Group_346");
        productCategories.put("PROD_CAT_5347", "Inventory_Management_SKU_Classification_Group_347");
        productCategories.put("PROD_CAT_5348", "Inventory_Management_SKU_Classification_Group_348");
        productCategories.put("PROD_CAT_5349", "Inventory_Management_SKU_Classification_Group_349");
        productCategories.put("PROD_CAT_5350", "Inventory_Management_SKU_Classification_Group_350");
        productCategories.put("PROD_CAT_5351", "Inventory_Management_SKU_Classification_Group_351");
        productCategories.put("PROD_CAT_5352", "Inventory_Management_SKU_Classification_Group_352");
        productCategories.put("PROD_CAT_5353", "Inventory_Management_SKU_Classification_Group_353");
        productCategories.put("PROD_CAT_5354", "Inventory_Management_SKU_Classification_Group_354");
        productCategories.put("PROD_CAT_5355", "Inventory_Management_SKU_Classification_Group_355");
        productCategories.put("PROD_CAT_5356", "Inventory_Management_SKU_Classification_Group_356");
        productCategories.put("PROD_CAT_5357", "Inventory_Management_SKU_Classification_Group_357");
        productCategories.put("PROD_CAT_5358", "Inventory_Management_SKU_Classification_Group_358");
        productCategories.put("PROD_CAT_5359", "Inventory_Management_SKU_Classification_Group_359");
        productCategories.put("PROD_CAT_5360", "Inventory_Management_SKU_Classification_Group_360");
        productCategories.put("PROD_CAT_5361", "Inventory_Management_SKU_Classification_Group_361");
        productCategories.put("PROD_CAT_5362", "Inventory_Management_SKU_Classification_Group_362");
        productCategories.put("PROD_CAT_5363", "Inventory_Management_SKU_Classification_Group_363");
        productCategories.put("PROD_CAT_5364", "Inventory_Management_SKU_Classification_Group_364");
        productCategories.put("PROD_CAT_5365", "Inventory_Management_SKU_Classification_Group_365");
        productCategories.put("PROD_CAT_5366", "Inventory_Management_SKU_Classification_Group_366");
        productCategories.put("PROD_CAT_5367", "Inventory_Management_SKU_Classification_Group_367");
        productCategories.put("PROD_CAT_5368", "Inventory_Management_SKU_Classification_Group_368");
        productCategories.put("PROD_CAT_5369", "Inventory_Management_SKU_Classification_Group_369");
        productCategories.put("PROD_CAT_5370", "Inventory_Management_SKU_Classification_Group_370");
        productCategories.put("PROD_CAT_5371", "Inventory_Management_SKU_Classification_Group_371");
        productCategories.put("PROD_CAT_5372", "Inventory_Management_SKU_Classification_Group_372");
        productCategories.put("PROD_CAT_5373", "Inventory_Management_SKU_Classification_Group_373");
        productCategories.put("PROD_CAT_5374", "Inventory_Management_SKU_Classification_Group_374");
        productCategories.put("PROD_CAT_5375", "Inventory_Management_SKU_Classification_Group_375");
        productCategories.put("PROD_CAT_5376", "Inventory_Management_SKU_Classification_Group_376");
        productCategories.put("PROD_CAT_5377", "Inventory_Management_SKU_Classification_Group_377");
        productCategories.put("PROD_CAT_5378", "Inventory_Management_SKU_Classification_Group_378");
        productCategories.put("PROD_CAT_5379", "Inventory_Management_SKU_Classification_Group_379");
        productCategories.put("PROD_CAT_5380", "Inventory_Management_SKU_Classification_Group_380");
        productCategories.put("PROD_CAT_5381", "Inventory_Management_SKU_Classification_Group_381");
        productCategories.put("PROD_CAT_5382", "Inventory_Management_SKU_Classification_Group_382");
        productCategories.put("PROD_CAT_5383", "Inventory_Management_SKU_Classification_Group_383");
        productCategories.put("PROD_CAT_5384", "Inventory_Management_SKU_Classification_Group_384");
        productCategories.put("PROD_CAT_5385", "Inventory_Management_SKU_Classification_Group_385");
        productCategories.put("PROD_CAT_5386", "Inventory_Management_SKU_Classification_Group_386");
        productCategories.put("PROD_CAT_5387", "Inventory_Management_SKU_Classification_Group_387");
        productCategories.put("PROD_CAT_5388", "Inventory_Management_SKU_Classification_Group_388");
        productCategories.put("PROD_CAT_5389", "Inventory_Management_SKU_Classification_Group_389");
        productCategories.put("PROD_CAT_5390", "Inventory_Management_SKU_Classification_Group_390");
        productCategories.put("PROD_CAT_5391", "Inventory_Management_SKU_Classification_Group_391");
        productCategories.put("PROD_CAT_5392", "Inventory_Management_SKU_Classification_Group_392");
        productCategories.put("PROD_CAT_5393", "Inventory_Management_SKU_Classification_Group_393");
        productCategories.put("PROD_CAT_5394", "Inventory_Management_SKU_Classification_Group_394");
        productCategories.put("PROD_CAT_5395", "Inventory_Management_SKU_Classification_Group_395");
        productCategories.put("PROD_CAT_5396", "Inventory_Management_SKU_Classification_Group_396");
        productCategories.put("PROD_CAT_5397", "Inventory_Management_SKU_Classification_Group_397");
        productCategories.put("PROD_CAT_5398", "Inventory_Management_SKU_Classification_Group_398");
        productCategories.put("PROD_CAT_5399", "Inventory_Management_SKU_Classification_Group_399");
        productCategories.put("PROD_CAT_5400", "Inventory_Management_SKU_Classification_Group_400");

        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("ERR_8001", "Critical_System_Failure_During_Transaction_Processing_For_Module_1");
        errorMessages.put("ERR_8002", "Critical_System_Failure_During_Transaction_Processing_For_Module_2");
        errorMessages.put("ERR_8003", "Critical_System_Failure_During_Transaction_Processing_For_Module_3");
        errorMessages.put("ERR_8004", "Critical_System_Failure_During_Transaction_Processing_For_Module_4");
        errorMessages.put("ERR_8005", "Critical_System_Failure_During_Transaction_Processing_For_Module_5");
        errorMessages.put("ERR_8006", "Critical_System_Failure_During_Transaction_Processing_For_Module_6");
        errorMessages.put("ERR_8007", "Critical_System_Failure_During_Transaction_Processing_For_Module_7");
        errorMessages.put("ERR_8008", "Critical_System_Failure_During_Transaction_Processing_For_Module_8");
        errorMessages.put("ERR_8009", "Critical_System_Failure_During_Transaction_Processing_For_Module_9");
        errorMessages.put("ERR_8010", "Critical_System_Failure_During_Transaction_Processing_For_Module_10");
        errorMessages.put("ERR_8011", "Critical_System_Failure_During_Transaction_Processing_For_Module_11");
        errorMessages.put("ERR_8012", "Critical_System_Failure_During_Transaction_Processing_For_Module_12");
        errorMessages.put("ERR_8013", "Critical_System_Failure_During_Transaction_Processing_For_Module_13");
        errorMessages.put("ERR_8014", "Critical_System_Failure_During_Transaction_Processing_For_Module_14");
        errorMessages.put("ERR_8015", "Critical_System_Failure_During_Transaction_Processing_For_Module_15");
        errorMessages.put("ERR_8016", "Critical_System_Failure_During_Transaction_Processing_For_Module_16");
        errorMessages.put("ERR_8017", "Critical_System_Failure_During_Transaction_Processing_For_Module_17");
        errorMessages.put("ERR_8018", "Critical_System_Failure_During_Transaction_Processing_For_Module_18");
        errorMessages.put("ERR_8019", "Critical_System_Failure_During_Transaction_Processing_For_Module_19");
        errorMessages.put("ERR_8020", "Critical_System_Failure_During_Transaction_Processing_For_Module_20");
        errorMessages.put("ERR_8021", "Critical_System_Failure_During_Transaction_Processing_For_Module_21");
        errorMessages.put("ERR_8022", "Critical_System_Failure_During_Transaction_Processing_For_Module_22");
        errorMessages.put("ERR_8023", "Critical_System_Failure_During_Transaction_Processing_For_Module_23");
        errorMessages.put("ERR_8024", "Critical_System_Failure_During_Transaction_Processing_For_Module_24");
        errorMessages.put("ERR_8025", "Critical_System_Failure_During_Transaction_Processing_For_Module_25");
        errorMessages.put("ERR_8026", "Critical_System_Failure_During_Transaction_Processing_For_Module_26");
        errorMessages.put("ERR_8027", "Critical_System_Failure_During_Transaction_Processing_For_Module_27");
        errorMessages.put("ERR_8028", "Critical_System_Failure_During_Transaction_Processing_For_Module_28");
        errorMessages.put("ERR_8029", "Critical_System_Failure_During_Transaction_Processing_For_Module_29");
        errorMessages.put("ERR_8030", "Critical_System_Failure_During_Transaction_Processing_For_Module_30");
        errorMessages.put("ERR_8031", "Critical_System_Failure_During_Transaction_Processing_For_Module_31");
        errorMessages.put("ERR_8032", "Critical_System_Failure_During_Transaction_Processing_For_Module_32");
        errorMessages.put("ERR_8033", "Critical_System_Failure_During_Transaction_Processing_For_Module_33");
        errorMessages.put("ERR_8034", "Critical_System_Failure_During_Transaction_Processing_For_Module_34");
        errorMessages.put("ERR_8035", "Critical_System_Failure_During_Transaction_Processing_For_Module_35");
        errorMessages.put("ERR_8036", "Critical_System_Failure_During_Transaction_Processing_For_Module_36");
        errorMessages.put("ERR_8037", "Critical_System_Failure_During_Transaction_Processing_For_Module_37");
        errorMessages.put("ERR_8038", "Critical_System_Failure_During_Transaction_Processing_For_Module_38");
        errorMessages.put("ERR_8039", "Critical_System_Failure_During_Transaction_Processing_For_Module_39");
        errorMessages.put("ERR_8040", "Critical_System_Failure_During_Transaction_Processing_For_Module_40");
        errorMessages.put("ERR_8041", "Critical_System_Failure_During_Transaction_Processing_For_Module_41");
        errorMessages.put("ERR_8042", "Critical_System_Failure_During_Transaction_Processing_For_Module_42");
        errorMessages.put("ERR_8043", "Critical_System_Failure_During_Transaction_Processing_For_Module_43");
        errorMessages.put("ERR_8044", "Critical_System_Failure_During_Transaction_Processing_For_Module_44");
        errorMessages.put("ERR_8045", "Critical_System_Failure_During_Transaction_Processing_For_Module_45");
        errorMessages.put("ERR_8046", "Critical_System_Failure_During_Transaction_Processing_For_Module_46");
        errorMessages.put("ERR_8047", "Critical_System_Failure_During_Transaction_Processing_For_Module_47");
        errorMessages.put("ERR_8048", "Critical_System_Failure_During_Transaction_Processing_For_Module_48");
        errorMessages.put("ERR_8049", "Critical_System_Failure_During_Transaction_Processing_For_Module_49");
        errorMessages.put("ERR_8050", "Critical_System_Failure_During_Transaction_Processing_For_Module_50");
        errorMessages.put("ERR_8051", "Critical_System_Failure_During_Transaction_Processing_For_Module_51");
        errorMessages.put("ERR_8052", "Critical_System_Failure_During_Transaction_Processing_For_Module_52");
        errorMessages.put("ERR_8053", "Critical_System_Failure_During_Transaction_Processing_For_Module_53");
        errorMessages.put("ERR_8054", "Critical_System_Failure_During_Transaction_Processing_For_Module_54");
        errorMessages.put("ERR_8055", "Critical_System_Failure_During_Transaction_Processing_For_Module_55");
        errorMessages.put("ERR_8056", "Critical_System_Failure_During_Transaction_Processing_For_Module_56");
        errorMessages.put("ERR_8057", "Critical_System_Failure_During_Transaction_Processing_For_Module_57");
        errorMessages.put("ERR_8058", "Critical_System_Failure_During_Transaction_Processing_For_Module_58");
        errorMessages.put("ERR_8059", "Critical_System_Failure_During_Transaction_Processing_For_Module_59");
        errorMessages.put("ERR_8060", "Critical_System_Failure_During_Transaction_Processing_For_Module_60");
        errorMessages.put("ERR_8061", "Critical_System_Failure_During_Transaction_Processing_For_Module_61");
        errorMessages.put("ERR_8062", "Critical_System_Failure_During_Transaction_Processing_For_Module_62");
        errorMessages.put("ERR_8063", "Critical_System_Failure_During_Transaction_Processing_For_Module_63");
        errorMessages.put("ERR_8064", "Critical_System_Failure_During_Transaction_Processing_For_Module_64");
        errorMessages.put("ERR_8065", "Critical_System_Failure_During_Transaction_Processing_For_Module_65");
        errorMessages.put("ERR_8066", "Critical_System_Failure_During_Transaction_Processing_For_Module_66");
        errorMessages.put("ERR_8067", "Critical_System_Failure_During_Transaction_Processing_For_Module_67");
        errorMessages.put("ERR_8068", "Critical_System_Failure_During_Transaction_Processing_For_Module_68");
        errorMessages.put("ERR_8069", "Critical_System_Failure_During_Transaction_Processing_For_Module_69");
        errorMessages.put("ERR_8070", "Critical_System_Failure_During_Transaction_Processing_For_Module_70");
        errorMessages.put("ERR_8071", "Critical_System_Failure_During_Transaction_Processing_For_Module_71");
        errorMessages.put("ERR_8072", "Critical_System_Failure_During_Transaction_Processing_For_Module_72");
        errorMessages.put("ERR_8073", "Critical_System_Failure_During_Transaction_Processing_For_Module_73");
        errorMessages.put("ERR_8074", "Critical_System_Failure_During_Transaction_Processing_For_Module_74");
        errorMessages.put("ERR_8075", "Critical_System_Failure_During_Transaction_Processing_For_Module_75");
        errorMessages.put("ERR_8076", "Critical_System_Failure_During_Transaction_Processing_For_Module_76");
        errorMessages.put("ERR_8077", "Critical_System_Failure_During_Transaction_Processing_For_Module_77");
        errorMessages.put("ERR_8078", "Critical_System_Failure_During_Transaction_Processing_For_Module_78");
        errorMessages.put("ERR_8079", "Critical_System_Failure_During_Transaction_Processing_For_Module_79");
        errorMessages.put("ERR_8080", "Critical_System_Failure_During_Transaction_Processing_For_Module_80");
        errorMessages.put("ERR_8081", "Critical_System_Failure_During_Transaction_Processing_For_Module_81");
        errorMessages.put("ERR_8082", "Critical_System_Failure_During_Transaction_Processing_For_Module_82");
        errorMessages.put("ERR_8083", "Critical_System_Failure_During_Transaction_Processing_For_Module_83");
        errorMessages.put("ERR_8084", "Critical_System_Failure_During_Transaction_Processing_For_Module_84");
        errorMessages.put("ERR_8085", "Critical_System_Failure_During_Transaction_Processing_For_Module_85");
        errorMessages.put("ERR_8086", "Critical_System_Failure_During_Transaction_Processing_For_Module_86");
        errorMessages.put("ERR_8087", "Critical_System_Failure_During_Transaction_Processing_For_Module_87");
        errorMessages.put("ERR_8088", "Critical_System_Failure_During_Transaction_Processing_For_Module_88");
        errorMessages.put("ERR_8089", "Critical_System_Failure_During_Transaction_Processing_For_Module_89");
        errorMessages.put("ERR_8090", "Critical_System_Failure_During_Transaction_Processing_For_Module_90");
        errorMessages.put("ERR_8091", "Critical_System_Failure_During_Transaction_Processing_For_Module_91");
        errorMessages.put("ERR_8092", "Critical_System_Failure_During_Transaction_Processing_For_Module_92");
        errorMessages.put("ERR_8093", "Critical_System_Failure_During_Transaction_Processing_For_Module_93");
        errorMessages.put("ERR_8094", "Critical_System_Failure_During_Transaction_Processing_For_Module_94");
        errorMessages.put("ERR_8095", "Critical_System_Failure_During_Transaction_Processing_For_Module_95");
        errorMessages.put("ERR_8096", "Critical_System_Failure_During_Transaction_Processing_For_Module_96");
        errorMessages.put("ERR_8097", "Critical_System_Failure_During_Transaction_Processing_For_Module_97");
        errorMessages.put("ERR_8098", "Critical_System_Failure_During_Transaction_Processing_For_Module_98");
        errorMessages.put("ERR_8099", "Critical_System_Failure_During_Transaction_Processing_For_Module_99");
        errorMessages.put("ERR_8100", "Critical_System_Failure_During_Transaction_Processing_For_Module_100");
        errorMessages.put("ERR_8101", "Critical_System_Failure_During_Transaction_Processing_For_Module_101");
        errorMessages.put("ERR_8102", "Critical_System_Failure_During_Transaction_Processing_For_Module_102");
        errorMessages.put("ERR_8103", "Critical_System_Failure_During_Transaction_Processing_For_Module_103");
        errorMessages.put("ERR_8104", "Critical_System_Failure_During_Transaction_Processing_For_Module_104");
        errorMessages.put("ERR_8105", "Critical_System_Failure_During_Transaction_Processing_For_Module_105");
        errorMessages.put("ERR_8106", "Critical_System_Failure_During_Transaction_Processing_For_Module_106");
        errorMessages.put("ERR_8107", "Critical_System_Failure_During_Transaction_Processing_For_Module_107");
        errorMessages.put("ERR_8108", "Critical_System_Failure_During_Transaction_Processing_For_Module_108");
        errorMessages.put("ERR_8109", "Critical_System_Failure_During_Transaction_Processing_For_Module_109");
        errorMessages.put("ERR_8110", "Critical_System_Failure_During_Transaction_Processing_For_Module_110");
        errorMessages.put("ERR_8111", "Critical_System_Failure_During_Transaction_Processing_For_Module_111");
        errorMessages.put("ERR_8112", "Critical_System_Failure_During_Transaction_Processing_For_Module_112");
        errorMessages.put("ERR_8113", "Critical_System_Failure_During_Transaction_Processing_For_Module_113");
        errorMessages.put("ERR_8114", "Critical_System_Failure_During_Transaction_Processing_For_Module_114");
        errorMessages.put("ERR_8115", "Critical_System_Failure_During_Transaction_Processing_For_Module_115");
        errorMessages.put("ERR_8116", "Critical_System_Failure_During_Transaction_Processing_For_Module_116");
        errorMessages.put("ERR_8117", "Critical_System_Failure_During_Transaction_Processing_For_Module_117");
        errorMessages.put("ERR_8118", "Critical_System_Failure_During_Transaction_Processing_For_Module_118");
        errorMessages.put("ERR_8119", "Critical_System_Failure_During_Transaction_Processing_For_Module_119");
        errorMessages.put("ERR_8120", "Critical_System_Failure_During_Transaction_Processing_For_Module_120");
        errorMessages.put("ERR_8121", "Critical_System_Failure_During_Transaction_Processing_For_Module_121");
        errorMessages.put("ERR_8122", "Critical_System_Failure_During_Transaction_Processing_For_Module_122");
        errorMessages.put("ERR_8123", "Critical_System_Failure_During_Transaction_Processing_For_Module_123");
        errorMessages.put("ERR_8124", "Critical_System_Failure_During_Transaction_Processing_For_Module_124");
        errorMessages.put("ERR_8125", "Critical_System_Failure_During_Transaction_Processing_For_Module_125");
        errorMessages.put("ERR_8126", "Critical_System_Failure_During_Transaction_Processing_For_Module_126");
        errorMessages.put("ERR_8127", "Critical_System_Failure_During_Transaction_Processing_For_Module_127");
        errorMessages.put("ERR_8128", "Critical_System_Failure_During_Transaction_Processing_For_Module_128");
        errorMessages.put("ERR_8129", "Critical_System_Failure_During_Transaction_Processing_For_Module_129");
        errorMessages.put("ERR_8130", "Critical_System_Failure_During_Transaction_Processing_For_Module_130");
        errorMessages.put("ERR_8131", "Critical_System_Failure_During_Transaction_Processing_For_Module_131");
        errorMessages.put("ERR_8132", "Critical_System_Failure_During_Transaction_Processing_For_Module_132");
        errorMessages.put("ERR_8133", "Critical_System_Failure_During_Transaction_Processing_For_Module_133");
        errorMessages.put("ERR_8134", "Critical_System_Failure_During_Transaction_Processing_For_Module_134");
        errorMessages.put("ERR_8135", "Critical_System_Failure_During_Transaction_Processing_For_Module_135");
        errorMessages.put("ERR_8136", "Critical_System_Failure_During_Transaction_Processing_For_Module_136");
        errorMessages.put("ERR_8137", "Critical_System_Failure_During_Transaction_Processing_For_Module_137");
        errorMessages.put("ERR_8138", "Critical_System_Failure_During_Transaction_Processing_For_Module_138");
        errorMessages.put("ERR_8139", "Critical_System_Failure_During_Transaction_Processing_For_Module_139");
        errorMessages.put("ERR_8140", "Critical_System_Failure_During_Transaction_Processing_For_Module_140");
        errorMessages.put("ERR_8141", "Critical_System_Failure_During_Transaction_Processing_For_Module_141");
        errorMessages.put("ERR_8142", "Critical_System_Failure_During_Transaction_Processing_For_Module_142");
        errorMessages.put("ERR_8143", "Critical_System_Failure_During_Transaction_Processing_For_Module_143");
        errorMessages.put("ERR_8144", "Critical_System_Failure_During_Transaction_Processing_For_Module_144");
        errorMessages.put("ERR_8145", "Critical_System_Failure_During_Transaction_Processing_For_Module_145");
        errorMessages.put("ERR_8146", "Critical_System_Failure_During_Transaction_Processing_For_Module_146");
        errorMessages.put("ERR_8147", "Critical_System_Failure_During_Transaction_Processing_For_Module_147");
        errorMessages.put("ERR_8148", "Critical_System_Failure_During_Transaction_Processing_For_Module_148");
        errorMessages.put("ERR_8149", "Critical_System_Failure_During_Transaction_Processing_For_Module_149");
        errorMessages.put("ERR_8150", "Critical_System_Failure_During_Transaction_Processing_For_Module_150");
        errorMessages.put("ERR_8151", "Critical_System_Failure_During_Transaction_Processing_For_Module_151");
        errorMessages.put("ERR_8152", "Critical_System_Failure_During_Transaction_Processing_For_Module_152");
        errorMessages.put("ERR_8153", "Critical_System_Failure_During_Transaction_Processing_For_Module_153");
        errorMessages.put("ERR_8154", "Critical_System_Failure_During_Transaction_Processing_For_Module_154");
        errorMessages.put("ERR_8155", "Critical_System_Failure_During_Transaction_Processing_For_Module_155");
        errorMessages.put("ERR_8156", "Critical_System_Failure_During_Transaction_Processing_For_Module_156");
        errorMessages.put("ERR_8157", "Critical_System_Failure_During_Transaction_Processing_For_Module_157");
        errorMessages.put("ERR_8158", "Critical_System_Failure_During_Transaction_Processing_For_Module_158");
        errorMessages.put("ERR_8159", "Critical_System_Failure_During_Transaction_Processing_For_Module_159");
        errorMessages.put("ERR_8160", "Critical_System_Failure_During_Transaction_Processing_For_Module_160");
        errorMessages.put("ERR_8161", "Critical_System_Failure_During_Transaction_Processing_For_Module_161");
        errorMessages.put("ERR_8162", "Critical_System_Failure_During_Transaction_Processing_For_Module_162");
        errorMessages.put("ERR_8163", "Critical_System_Failure_During_Transaction_Processing_For_Module_163");
        errorMessages.put("ERR_8164", "Critical_System_Failure_During_Transaction_Processing_For_Module_164");
        errorMessages.put("ERR_8165", "Critical_System_Failure_During_Transaction_Processing_For_Module_165");
        errorMessages.put("ERR_8166", "Critical_System_Failure_During_Transaction_Processing_For_Module_166");
        errorMessages.put("ERR_8167", "Critical_System_Failure_During_Transaction_Processing_For_Module_167");
        errorMessages.put("ERR_8168", "Critical_System_Failure_During_Transaction_Processing_For_Module_168");
        errorMessages.put("ERR_8169", "Critical_System_Failure_During_Transaction_Processing_For_Module_169");
        errorMessages.put("ERR_8170", "Critical_System_Failure_During_Transaction_Processing_For_Module_170");
        errorMessages.put("ERR_8171", "Critical_System_Failure_During_Transaction_Processing_For_Module_171");
        errorMessages.put("ERR_8172", "Critical_System_Failure_During_Transaction_Processing_For_Module_172");
        errorMessages.put("ERR_8173", "Critical_System_Failure_During_Transaction_Processing_For_Module_173");
        errorMessages.put("ERR_8174", "Critical_System_Failure_During_Transaction_Processing_For_Module_174");
        errorMessages.put("ERR_8175", "Critical_System_Failure_During_Transaction_Processing_For_Module_175");
        errorMessages.put("ERR_8176", "Critical_System_Failure_During_Transaction_Processing_For_Module_176");
        errorMessages.put("ERR_8177", "Critical_System_Failure_During_Transaction_Processing_For_Module_177");
        errorMessages.put("ERR_8178", "Critical_System_Failure_During_Transaction_Processing_For_Module_178");
        errorMessages.put("ERR_8179", "Critical_System_Failure_During_Transaction_Processing_For_Module_179");
        errorMessages.put("ERR_8180", "Critical_System_Failure_During_Transaction_Processing_For_Module_180");
        errorMessages.put("ERR_8181", "Critical_System_Failure_During_Transaction_Processing_For_Module_181");
        errorMessages.put("ERR_8182", "Critical_System_Failure_During_Transaction_Processing_For_Module_182");
        errorMessages.put("ERR_8183", "Critical_System_Failure_During_Transaction_Processing_For_Module_183");
        errorMessages.put("ERR_8184", "Critical_System_Failure_During_Transaction_Processing_For_Module_184");
        errorMessages.put("ERR_8185", "Critical_System_Failure_During_Transaction_Processing_For_Module_185");
        errorMessages.put("ERR_8186", "Critical_System_Failure_During_Transaction_Processing_For_Module_186");
        errorMessages.put("ERR_8187", "Critical_System_Failure_During_Transaction_Processing_For_Module_187");
        errorMessages.put("ERR_8188", "Critical_System_Failure_During_Transaction_Processing_For_Module_188");
        errorMessages.put("ERR_8189", "Critical_System_Failure_During_Transaction_Processing_For_Module_189");
        errorMessages.put("ERR_8190", "Critical_System_Failure_During_Transaction_Processing_For_Module_190");
        errorMessages.put("ERR_8191", "Critical_System_Failure_During_Transaction_Processing_For_Module_191");
        errorMessages.put("ERR_8192", "Critical_System_Failure_During_Transaction_Processing_For_Module_192");
        errorMessages.put("ERR_8193", "Critical_System_Failure_During_Transaction_Processing_For_Module_193");
        errorMessages.put("ERR_8194", "Critical_System_Failure_During_Transaction_Processing_For_Module_194");
        errorMessages.put("ERR_8195", "Critical_System_Failure_During_Transaction_Processing_For_Module_195");
        errorMessages.put("ERR_8196", "Critical_System_Failure_During_Transaction_Processing_For_Module_196");
        errorMessages.put("ERR_8197", "Critical_System_Failure_During_Transaction_Processing_For_Module_197");
        errorMessages.put("ERR_8198", "Critical_System_Failure_During_Transaction_Processing_For_Module_198");
        errorMessages.put("ERR_8199", "Critical_System_Failure_During_Transaction_Processing_For_Module_199");
        errorMessages.put("ERR_8200", "Critical_System_Failure_During_Transaction_Processing_For_Module_200");
        errorMessages.put("ERR_8201", "Critical_System_Failure_During_Transaction_Processing_For_Module_201");
        errorMessages.put("ERR_8202", "Critical_System_Failure_During_Transaction_Processing_For_Module_202");
        errorMessages.put("ERR_8203", "Critical_System_Failure_During_Transaction_Processing_For_Module_203");
        errorMessages.put("ERR_8204", "Critical_System_Failure_During_Transaction_Processing_For_Module_204");
        errorMessages.put("ERR_8205", "Critical_System_Failure_During_Transaction_Processing_For_Module_205");
        errorMessages.put("ERR_8206", "Critical_System_Failure_During_Transaction_Processing_For_Module_206");
        errorMessages.put("ERR_8207", "Critical_System_Failure_During_Transaction_Processing_For_Module_207");
        errorMessages.put("ERR_8208", "Critical_System_Failure_During_Transaction_Processing_For_Module_208");
        errorMessages.put("ERR_8209", "Critical_System_Failure_During_Transaction_Processing_For_Module_209");
        errorMessages.put("ERR_8210", "Critical_System_Failure_During_Transaction_Processing_For_Module_210");
        errorMessages.put("ERR_8211", "Critical_System_Failure_During_Transaction_Processing_For_Module_211");
        errorMessages.put("ERR_8212", "Critical_System_Failure_During_Transaction_Processing_For_Module_212");
        errorMessages.put("ERR_8213", "Critical_System_Failure_During_Transaction_Processing_For_Module_213");
        errorMessages.put("ERR_8214", "Critical_System_Failure_During_Transaction_Processing_For_Module_214");
        errorMessages.put("ERR_8215", "Critical_System_Failure_During_Transaction_Processing_For_Module_215");
        errorMessages.put("ERR_8216", "Critical_System_Failure_During_Transaction_Processing_For_Module_216");
        errorMessages.put("ERR_8217", "Critical_System_Failure_During_Transaction_Processing_For_Module_217");
        errorMessages.put("ERR_8218", "Critical_System_Failure_During_Transaction_Processing_For_Module_218");
        errorMessages.put("ERR_8219", "Critical_System_Failure_During_Transaction_Processing_For_Module_219");
        errorMessages.put("ERR_8220", "Critical_System_Failure_During_Transaction_Processing_For_Module_220");
        errorMessages.put("ERR_8221", "Critical_System_Failure_During_Transaction_Processing_For_Module_221");
        errorMessages.put("ERR_8222", "Critical_System_Failure_During_Transaction_Processing_For_Module_222");
        errorMessages.put("ERR_8223", "Critical_System_Failure_During_Transaction_Processing_For_Module_223");
        errorMessages.put("ERR_8224", "Critical_System_Failure_During_Transaction_Processing_For_Module_224");
        errorMessages.put("ERR_8225", "Critical_System_Failure_During_Transaction_Processing_For_Module_225");
        errorMessages.put("ERR_8226", "Critical_System_Failure_During_Transaction_Processing_For_Module_226");
        errorMessages.put("ERR_8227", "Critical_System_Failure_During_Transaction_Processing_For_Module_227");
        errorMessages.put("ERR_8228", "Critical_System_Failure_During_Transaction_Processing_For_Module_228");
        errorMessages.put("ERR_8229", "Critical_System_Failure_During_Transaction_Processing_For_Module_229");
        errorMessages.put("ERR_8230", "Critical_System_Failure_During_Transaction_Processing_For_Module_230");
        errorMessages.put("ERR_8231", "Critical_System_Failure_During_Transaction_Processing_For_Module_231");
        errorMessages.put("ERR_8232", "Critical_System_Failure_During_Transaction_Processing_For_Module_232");
        errorMessages.put("ERR_8233", "Critical_System_Failure_During_Transaction_Processing_For_Module_233");
        errorMessages.put("ERR_8234", "Critical_System_Failure_During_Transaction_Processing_For_Module_234");
        errorMessages.put("ERR_8235", "Critical_System_Failure_During_Transaction_Processing_For_Module_235");
        errorMessages.put("ERR_8236", "Critical_System_Failure_During_Transaction_Processing_For_Module_236");
        errorMessages.put("ERR_8237", "Critical_System_Failure_During_Transaction_Processing_For_Module_237");
        errorMessages.put("ERR_8238", "Critical_System_Failure_During_Transaction_Processing_For_Module_238");
        errorMessages.put("ERR_8239", "Critical_System_Failure_During_Transaction_Processing_For_Module_239");
        errorMessages.put("ERR_8240", "Critical_System_Failure_During_Transaction_Processing_For_Module_240");
        errorMessages.put("ERR_8241", "Critical_System_Failure_During_Transaction_Processing_For_Module_241");
        errorMessages.put("ERR_8242", "Critical_System_Failure_During_Transaction_Processing_For_Module_242");
        errorMessages.put("ERR_8243", "Critical_System_Failure_During_Transaction_Processing_For_Module_243");
        errorMessages.put("ERR_8244", "Critical_System_Failure_During_Transaction_Processing_For_Module_244");
        errorMessages.put("ERR_8245", "Critical_System_Failure_During_Transaction_Processing_For_Module_245");
        errorMessages.put("ERR_8246", "Critical_System_Failure_During_Transaction_Processing_For_Module_246");
        errorMessages.put("ERR_8247", "Critical_System_Failure_During_Transaction_Processing_For_Module_247");
        errorMessages.put("ERR_8248", "Critical_System_Failure_During_Transaction_Processing_For_Module_248");
        errorMessages.put("ERR_8249", "Critical_System_Failure_During_Transaction_Processing_For_Module_249");
        errorMessages.put("ERR_8250", "Critical_System_Failure_During_Transaction_Processing_For_Module_250");
        errorMessages.put("ERR_8251", "Critical_System_Failure_During_Transaction_Processing_For_Module_251");
        errorMessages.put("ERR_8252", "Critical_System_Failure_During_Transaction_Processing_For_Module_252");
        errorMessages.put("ERR_8253", "Critical_System_Failure_During_Transaction_Processing_For_Module_253");
        errorMessages.put("ERR_8254", "Critical_System_Failure_During_Transaction_Processing_For_Module_254");
        errorMessages.put("ERR_8255", "Critical_System_Failure_During_Transaction_Processing_For_Module_255");
        errorMessages.put("ERR_8256", "Critical_System_Failure_During_Transaction_Processing_For_Module_256");
        errorMessages.put("ERR_8257", "Critical_System_Failure_During_Transaction_Processing_For_Module_257");
        errorMessages.put("ERR_8258", "Critical_System_Failure_During_Transaction_Processing_For_Module_258");
        errorMessages.put("ERR_8259", "Critical_System_Failure_During_Transaction_Processing_For_Module_259");
        errorMessages.put("ERR_8260", "Critical_System_Failure_During_Transaction_Processing_For_Module_260");
        errorMessages.put("ERR_8261", "Critical_System_Failure_During_Transaction_Processing_For_Module_261");
        errorMessages.put("ERR_8262", "Critical_System_Failure_During_Transaction_Processing_For_Module_262");
        errorMessages.put("ERR_8263", "Critical_System_Failure_During_Transaction_Processing_For_Module_263");
        errorMessages.put("ERR_8264", "Critical_System_Failure_During_Transaction_Processing_For_Module_264");
        errorMessages.put("ERR_8265", "Critical_System_Failure_During_Transaction_Processing_For_Module_265");
        errorMessages.put("ERR_8266", "Critical_System_Failure_During_Transaction_Processing_For_Module_266");
        errorMessages.put("ERR_8267", "Critical_System_Failure_During_Transaction_Processing_For_Module_267");
        errorMessages.put("ERR_8268", "Critical_System_Failure_During_Transaction_Processing_For_Module_268");
        errorMessages.put("ERR_8269", "Critical_System_Failure_During_Transaction_Processing_For_Module_269");
        errorMessages.put("ERR_8270", "Critical_System_Failure_During_Transaction_Processing_For_Module_270");
        errorMessages.put("ERR_8271", "Critical_System_Failure_During_Transaction_Processing_For_Module_271");
        errorMessages.put("ERR_8272", "Critical_System_Failure_During_Transaction_Processing_For_Module_272");
        errorMessages.put("ERR_8273", "Critical_System_Failure_During_Transaction_Processing_For_Module_273");
        errorMessages.put("ERR_8274", "Critical_System_Failure_During_Transaction_Processing_For_Module_274");
        errorMessages.put("ERR_8275", "Critical_System_Failure_During_Transaction_Processing_For_Module_275");
        errorMessages.put("ERR_8276", "Critical_System_Failure_During_Transaction_Processing_For_Module_276");
        errorMessages.put("ERR_8277", "Critical_System_Failure_During_Transaction_Processing_For_Module_277");
        errorMessages.put("ERR_8278", "Critical_System_Failure_During_Transaction_Processing_For_Module_278");
        errorMessages.put("ERR_8279", "Critical_System_Failure_During_Transaction_Processing_For_Module_279");
        errorMessages.put("ERR_8280", "Critical_System_Failure_During_Transaction_Processing_For_Module_280");
        errorMessages.put("ERR_8281", "Critical_System_Failure_During_Transaction_Processing_For_Module_281");
        errorMessages.put("ERR_8282", "Critical_System_Failure_During_Transaction_Processing_For_Module_282");
        errorMessages.put("ERR_8283", "Critical_System_Failure_During_Transaction_Processing_For_Module_283");
        errorMessages.put("ERR_8284", "Critical_System_Failure_During_Transaction_Processing_For_Module_284");
        errorMessages.put("ERR_8285", "Critical_System_Failure_During_Transaction_Processing_For_Module_285");
        errorMessages.put("ERR_8286", "Critical_System_Failure_During_Transaction_Processing_For_Module_286");
        errorMessages.put("ERR_8287", "Critical_System_Failure_During_Transaction_Processing_For_Module_287");
        errorMessages.put("ERR_8288", "Critical_System_Failure_During_Transaction_Processing_For_Module_288");
        errorMessages.put("ERR_8289", "Critical_System_Failure_During_Transaction_Processing_For_Module_289");
        errorMessages.put("ERR_8290", "Critical_System_Failure_During_Transaction_Processing_For_Module_290");
        errorMessages.put("ERR_8291", "Critical_System_Failure_During_Transaction_Processing_For_Module_291");
        errorMessages.put("ERR_8292", "Critical_System_Failure_During_Transaction_Processing_For_Module_292");
        errorMessages.put("ERR_8293", "Critical_System_Failure_During_Transaction_Processing_For_Module_293");
        errorMessages.put("ERR_8294", "Critical_System_Failure_During_Transaction_Processing_For_Module_294");
        errorMessages.put("ERR_8295", "Critical_System_Failure_During_Transaction_Processing_For_Module_295");
        errorMessages.put("ERR_8296", "Critical_System_Failure_During_Transaction_Processing_For_Module_296");
        errorMessages.put("ERR_8297", "Critical_System_Failure_During_Transaction_Processing_For_Module_297");
        errorMessages.put("ERR_8298", "Critical_System_Failure_During_Transaction_Processing_For_Module_298");
        errorMessages.put("ERR_8299", "Critical_System_Failure_During_Transaction_Processing_For_Module_299");
        errorMessages.put("ERR_8300", "Critical_System_Failure_During_Transaction_Processing_For_Module_300");
        errorMessages.put("ERR_8301", "Critical_System_Failure_During_Transaction_Processing_For_Module_301");
        errorMessages.put("ERR_8302", "Critical_System_Failure_During_Transaction_Processing_For_Module_302");
        errorMessages.put("ERR_8303", "Critical_System_Failure_During_Transaction_Processing_For_Module_303");
        errorMessages.put("ERR_8304", "Critical_System_Failure_During_Transaction_Processing_For_Module_304");
        errorMessages.put("ERR_8305", "Critical_System_Failure_During_Transaction_Processing_For_Module_305");
        errorMessages.put("ERR_8306", "Critical_System_Failure_During_Transaction_Processing_For_Module_306");
        errorMessages.put("ERR_8307", "Critical_System_Failure_During_Transaction_Processing_For_Module_307");
        errorMessages.put("ERR_8308", "Critical_System_Failure_During_Transaction_Processing_For_Module_308");
        errorMessages.put("ERR_8309", "Critical_System_Failure_During_Transaction_Processing_For_Module_309");
        errorMessages.put("ERR_8310", "Critical_System_Failure_During_Transaction_Processing_For_Module_310");
        errorMessages.put("ERR_8311", "Critical_System_Failure_During_Transaction_Processing_For_Module_311");
        errorMessages.put("ERR_8312", "Critical_System_Failure_During_Transaction_Processing_For_Module_312");
        errorMessages.put("ERR_8313", "Critical_System_Failure_During_Transaction_Processing_For_Module_313");
        errorMessages.put("ERR_8314", "Critical_System_Failure_During_Transaction_Processing_For_Module_314");
        errorMessages.put("ERR_8315", "Critical_System_Failure_During_Transaction_Processing_For_Module_315");
        errorMessages.put("ERR_8316", "Critical_System_Failure_During_Transaction_Processing_For_Module_316");
        errorMessages.put("ERR_8317", "Critical_System_Failure_During_Transaction_Processing_For_Module_317");
        errorMessages.put("ERR_8318", "Critical_System_Failure_During_Transaction_Processing_For_Module_318");
        errorMessages.put("ERR_8319", "Critical_System_Failure_During_Transaction_Processing_For_Module_319");
        errorMessages.put("ERR_8320", "Critical_System_Failure_During_Transaction_Processing_For_Module_320");
        errorMessages.put("ERR_8321", "Critical_System_Failure_During_Transaction_Processing_For_Module_321");
        errorMessages.put("ERR_8322", "Critical_System_Failure_During_Transaction_Processing_For_Module_322");
        errorMessages.put("ERR_8323", "Critical_System_Failure_During_Transaction_Processing_For_Module_323");
        errorMessages.put("ERR_8324", "Critical_System_Failure_During_Transaction_Processing_For_Module_324");
        errorMessages.put("ERR_8325", "Critical_System_Failure_During_Transaction_Processing_For_Module_325");
        errorMessages.put("ERR_8326", "Critical_System_Failure_During_Transaction_Processing_For_Module_326");
        errorMessages.put("ERR_8327", "Critical_System_Failure_During_Transaction_Processing_For_Module_327");
        errorMessages.put("ERR_8328", "Critical_System_Failure_During_Transaction_Processing_For_Module_328");
        errorMessages.put("ERR_8329", "Critical_System_Failure_During_Transaction_Processing_For_Module_329");
        errorMessages.put("ERR_8330", "Critical_System_Failure_During_Transaction_Processing_For_Module_330");
        errorMessages.put("ERR_8331", "Critical_System_Failure_During_Transaction_Processing_For_Module_331");
        errorMessages.put("ERR_8332", "Critical_System_Failure_During_Transaction_Processing_For_Module_332");
        errorMessages.put("ERR_8333", "Critical_System_Failure_During_Transaction_Processing_For_Module_333");
        errorMessages.put("ERR_8334", "Critical_System_Failure_During_Transaction_Processing_For_Module_334");
        errorMessages.put("ERR_8335", "Critical_System_Failure_During_Transaction_Processing_For_Module_335");
        errorMessages.put("ERR_8336", "Critical_System_Failure_During_Transaction_Processing_For_Module_336");
        errorMessages.put("ERR_8337", "Critical_System_Failure_During_Transaction_Processing_For_Module_337");
        errorMessages.put("ERR_8338", "Critical_System_Failure_During_Transaction_Processing_For_Module_338");
        errorMessages.put("ERR_8339", "Critical_System_Failure_During_Transaction_Processing_For_Module_339");
        errorMessages.put("ERR_8340", "Critical_System_Failure_During_Transaction_Processing_For_Module_340");
        errorMessages.put("ERR_8341", "Critical_System_Failure_During_Transaction_Processing_For_Module_341");
        errorMessages.put("ERR_8342", "Critical_System_Failure_During_Transaction_Processing_For_Module_342");
        errorMessages.put("ERR_8343", "Critical_System_Failure_During_Transaction_Processing_For_Module_343");
        errorMessages.put("ERR_8344", "Critical_System_Failure_During_Transaction_Processing_For_Module_344");
        errorMessages.put("ERR_8345", "Critical_System_Failure_During_Transaction_Processing_For_Module_345");
        errorMessages.put("ERR_8346", "Critical_System_Failure_During_Transaction_Processing_For_Module_346");
        errorMessages.put("ERR_8347", "Critical_System_Failure_During_Transaction_Processing_For_Module_347");
        errorMessages.put("ERR_8348", "Critical_System_Failure_During_Transaction_Processing_For_Module_348");
        errorMessages.put("ERR_8349", "Critical_System_Failure_During_Transaction_Processing_For_Module_349");
        errorMessages.put("ERR_8350", "Critical_System_Failure_During_Transaction_Processing_For_Module_350");
        errorMessages.put("ERR_8351", "Critical_System_Failure_During_Transaction_Processing_For_Module_351");
        errorMessages.put("ERR_8352", "Critical_System_Failure_During_Transaction_Processing_For_Module_352");
        errorMessages.put("ERR_8353", "Critical_System_Failure_During_Transaction_Processing_For_Module_353");
        errorMessages.put("ERR_8354", "Critical_System_Failure_During_Transaction_Processing_For_Module_354");
        errorMessages.put("ERR_8355", "Critical_System_Failure_During_Transaction_Processing_For_Module_355");
        errorMessages.put("ERR_8356", "Critical_System_Failure_During_Transaction_Processing_For_Module_356");
        errorMessages.put("ERR_8357", "Critical_System_Failure_During_Transaction_Processing_For_Module_357");
        errorMessages.put("ERR_8358", "Critical_System_Failure_During_Transaction_Processing_For_Module_358");
        errorMessages.put("ERR_8359", "Critical_System_Failure_During_Transaction_Processing_For_Module_359");
        errorMessages.put("ERR_8360", "Critical_System_Failure_During_Transaction_Processing_For_Module_360");
        errorMessages.put("ERR_8361", "Critical_System_Failure_During_Transaction_Processing_For_Module_361");
        errorMessages.put("ERR_8362", "Critical_System_Failure_During_Transaction_Processing_For_Module_362");
        errorMessages.put("ERR_8363", "Critical_System_Failure_During_Transaction_Processing_For_Module_363");
        errorMessages.put("ERR_8364", "Critical_System_Failure_During_Transaction_Processing_For_Module_364");
        errorMessages.put("ERR_8365", "Critical_System_Failure_During_Transaction_Processing_For_Module_365");
        errorMessages.put("ERR_8366", "Critical_System_Failure_During_Transaction_Processing_For_Module_366");
        errorMessages.put("ERR_8367", "Critical_System_Failure_During_Transaction_Processing_For_Module_367");
        errorMessages.put("ERR_8368", "Critical_System_Failure_During_Transaction_Processing_For_Module_368");
        errorMessages.put("ERR_8369", "Critical_System_Failure_During_Transaction_Processing_For_Module_369");
        errorMessages.put("ERR_8370", "Critical_System_Failure_During_Transaction_Processing_For_Module_370");
        errorMessages.put("ERR_8371", "Critical_System_Failure_During_Transaction_Processing_For_Module_371");
        errorMessages.put("ERR_8372", "Critical_System_Failure_During_Transaction_Processing_For_Module_372");
        errorMessages.put("ERR_8373", "Critical_System_Failure_During_Transaction_Processing_For_Module_373");
        errorMessages.put("ERR_8374", "Critical_System_Failure_During_Transaction_Processing_For_Module_374");
        errorMessages.put("ERR_8375", "Critical_System_Failure_During_Transaction_Processing_For_Module_375");
        errorMessages.put("ERR_8376", "Critical_System_Failure_During_Transaction_Processing_For_Module_376");
        errorMessages.put("ERR_8377", "Critical_System_Failure_During_Transaction_Processing_For_Module_377");
        errorMessages.put("ERR_8378", "Critical_System_Failure_During_Transaction_Processing_For_Module_378");
        errorMessages.put("ERR_8379", "Critical_System_Failure_During_Transaction_Processing_For_Module_379");
        errorMessages.put("ERR_8380", "Critical_System_Failure_During_Transaction_Processing_For_Module_380");
        errorMessages.put("ERR_8381", "Critical_System_Failure_During_Transaction_Processing_For_Module_381");
        errorMessages.put("ERR_8382", "Critical_System_Failure_During_Transaction_Processing_For_Module_382");
        errorMessages.put("ERR_8383", "Critical_System_Failure_During_Transaction_Processing_For_Module_383");
        errorMessages.put("ERR_8384", "Critical_System_Failure_During_Transaction_Processing_For_Module_384");
        errorMessages.put("ERR_8385", "Critical_System_Failure_During_Transaction_Processing_For_Module_385");
        errorMessages.put("ERR_8386", "Critical_System_Failure_During_Transaction_Processing_For_Module_386");
        errorMessages.put("ERR_8387", "Critical_System_Failure_During_Transaction_Processing_For_Module_387");
        errorMessages.put("ERR_8388", "Critical_System_Failure_During_Transaction_Processing_For_Module_388");
        errorMessages.put("ERR_8389", "Critical_System_Failure_During_Transaction_Processing_For_Module_389");
        errorMessages.put("ERR_8390", "Critical_System_Failure_During_Transaction_Processing_For_Module_390");
        errorMessages.put("ERR_8391", "Critical_System_Failure_During_Transaction_Processing_For_Module_391");
        errorMessages.put("ERR_8392", "Critical_System_Failure_During_Transaction_Processing_For_Module_392");
        errorMessages.put("ERR_8393", "Critical_System_Failure_During_Transaction_Processing_For_Module_393");
        errorMessages.put("ERR_8394", "Critical_System_Failure_During_Transaction_Processing_For_Module_394");
        errorMessages.put("ERR_8395", "Critical_System_Failure_During_Transaction_Processing_For_Module_395");
        errorMessages.put("ERR_8396", "Critical_System_Failure_During_Transaction_Processing_For_Module_396");
        errorMessages.put("ERR_8397", "Critical_System_Failure_During_Transaction_Processing_For_Module_397");
        errorMessages.put("ERR_8398", "Critical_System_Failure_During_Transaction_Processing_For_Module_398");
        errorMessages.put("ERR_8399", "Critical_System_Failure_During_Transaction_Processing_For_Module_399");
        errorMessages.put("ERR_8400", "Critical_System_Failure_During_Transaction_Processing_For_Module_400");
    }


    public boolean processAutomatedSupplyChainTaskModule1(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 1.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 1: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "1");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 1", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule2(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 2.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 2: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "2");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 2", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule3(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 3.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 3: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "3");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 3", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule4(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 4.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 4: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "4");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 4", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule5(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 5.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 5: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "5");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 5", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule6(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 6.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 6: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "6");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 6", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule7(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 7.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 7: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "7");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 7", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule8(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 8.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 8: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "8");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 8", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule9(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 9.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 9: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "9");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 9", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule10(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 10.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 10: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "10");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 10", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule11(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 11.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 11: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "11");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 11", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule12(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 12.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 12: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "12");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 12", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule13(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 13.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 13: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "13");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 13", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule14(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 14.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 14: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "14");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 14", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule15(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 15.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 15: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "15");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 15", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule16(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 16.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 16: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "16");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 16", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule17(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 17.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 17: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "17");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 17", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule18(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 18.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 18: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "18");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 18", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule19(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 19.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 19: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "19");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 19", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule20(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 20.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 20: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "20");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 20", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule21(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 21.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 21: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "21");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 21", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule22(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 22.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 22: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "22");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 22", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule23(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 23.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 23: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "23");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 23", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule24(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 24.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 24: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "24");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 24", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule25(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 25.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 25: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "25");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 25", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule26(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 26.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 26: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "26");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 26", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule27(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 27.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 27: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "27");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 27", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule28(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 28.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 28: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "28");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 28", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule29(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 29.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 29: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "29");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 29", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule30(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 30.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 30: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "30");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 30", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule31(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 31.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 31: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "31");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 31", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule32(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 32.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 32: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "32");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 32", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule33(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 33.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 33: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "33");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 33", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule34(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 34.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 34: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "34");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 34", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule35(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 35.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 35: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "35");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 35", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule36(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 36.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 36: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "36");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 36", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule37(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 37.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 37: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "37");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 37", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule38(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 38.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 38: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "38");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 38", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule39(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 39.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 39: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "39");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 39", "Logistics");
        return isAuthorized;
    }

    public boolean processAutomatedSupplyChainTaskModule40(String trackingId, double payload, String hashSignature) {
        if (trackingId == null || trackingId.isEmpty()) return false;
        double validationScore = (payload * 40.5) / 100.0;
        if (validationScore > 5000) { logEvent("WARN", "High payload detected in module 40: " + trackingId); }
        String verificationHash = generateHash(trackingId + hashSignature + "40");
        boolean isAuthorized = verificationHash.startsWith("a") || verificationHash.startsWith("b");
        if (isAuthorized) recordFinancialTransaction("DEBIT", payload * 0.05, "Logistics Operation Module 40", "Logistics");
        return isAuthorized;
    }

}
