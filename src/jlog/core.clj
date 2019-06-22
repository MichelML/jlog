(ns jlog.core
  (:require [clojure.string :as str :refer [split includes? blank? lower-case upper-case]])
  (:require [clojure.java.shell :only [sh]])
  (:gen-class))

(use '[clojure.java.shell :only [sh]])

(def jlog-file "jlog.txt")

(defn valid-jlog-time?
  "Checks if the argument provided is of format HhMMm (basic support for JIRA time-log)."
  [timelog]
  (or (not (nil? (re-find #"^1d$" timelog))) 
      (not (nil? (re-find #"^([1-8][hH])?([1-6]{0,1}[0-9]{0,1}[mM])?$" timelog)))))


(defn valid-jira-issue?
  "Checks if the argument provided is a valid jira issue key."
  [issue]
  (not (nil? (re-find #"^[a-zA-Z]{3,4}-\d{1,4}$" issue))))

(defn space-hrs&mins
  "Adds a space between the hours log and minutes log if both are present."
  [timelog-raw]
  (let [timelog (lower-case timelog-raw)]
    (if (and (includes? timelog "h") (includes? timelog "m")) 
      (str/replace timelog "h" "h ") 
      timelog)))

;; clojure-based 
(defn get-jar-root
  "utility function to get the jar path without the jar file."
  [& [ns]]
  (str/replace (-> (or ns (class *ns*))
                   .getProtectionDomain .getCodeSource .getLocation .getPath) #"[^/]+$" ""))

(defn get-jira-issue
  "Get a Jira issue key from a branch name. Uses a future to make sure the shell command is executed before exiting the program."
  []
  (future 
    (let [out (:out (sh "hg" "branch"))]
      (if-not (blank? out) 
        (re-find #"[a-zA-Z]{3,4}-\d{1,4}" out)
        (throw 
          (Exception. "Cannot retrieve your current branch. Make sure you are in a repository's."))))))

(defn get-date
  "Gets a MM/dd/yyyy formatted date."
  []
  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date)))

(defn spit-log
  "Spits a Jira worklog into the worklogs file, containing the date, jira issue key, and message."
  [timelog issue message]
  (spit (str (get-jar-root) jlog-file) 
        (str (get-date) " --- " issue " *" (space-hrs&mins timelog) "* --- " message "\n")
        :append true)
  (println "Worklog saved successfully!"))

(defn print-worklogs
  "Lists/Prints worklogs contained in the worklogs file in the console."
  []
  (doseq [i ["\n" "Your Jira Worklogs:\n" (slurp (str (get-jar-root) jlog-file))]] (println i)))

(defn clean-worklog-file 
  "Clean the worklogs file of all existing worklogs."
  []
  (do 
    @(future (sh "rm" (str (get-jar-root) jlog-file)))
    @(future (sh "touch" (str (get-jar-root) jlog-file)))))

(defn jlog
  "Appends a worklog to the worklogs file. This is the jlog main function."
  [arg1 arg2 message]
  (cond 
    (= arg1 "-b") (if (valid-jlog-time? arg2)
      (spit-log arg2 @(get-jira-issue) message)
      (throw (Exception. "Invalid timelog format provided.")))
    (valid-jlog-time? arg1) (if (valid-jira-issue? arg2)
        (spit-log arg1 (upper-case arg2) message)
        (throw (Exception. "Invalid Jira issue key format provided.")))
    :else (throw (Exception. "Invalid timelog format provided."))))

(defn print-help
  "Prints the possible jlog commands to the console."
  []
  (println (str "\nValid commands for jlog are:\n\n"
                "   jlog -h                                            -     Prints the help menu to the console.\n"
                "   jlog -l                                            -     Prints worklogs contained in the worklogs file to the console.\n"
                "   jlog -c                                            -     Clean worklogs file from all existing worklogs.\n"
                "   jlog -o                                            -     Opens your worklogs file.\n"
                "   jlog -b <timelog> <message in quotes>              -     Writes a worklog to the worklogs file, retrieving the issue-key from your branch.\n"
                "   jlog <timelog> <issue key> <message in quotes>     -     Writes a worklog to the worklogs file using the provided information.\n")))

(defn -main [& args]
  @(future (sh "touch" (str (get-jar-root) jlog-file)))
  (cond 
    (= "-h" (first args)) (print-help)
    (= "-l" (first args)) (print-worklogs)
    (= "-c" (first args)) (clean-worklog-file)
    (= "-o" (first args)) (sh "open" (str (get-jar-root) jlog-file))
    (= (count (take 3 args)) 3) (apply jlog (take 3 args))
    :else ((println "Syntax error.") (print-help)))
  (shutdown-agents))
