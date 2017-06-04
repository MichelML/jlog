(ns jlog.core
  (:require [clojure.string :as str :refer [split includes? blank? lower-case upper-case]])
  (:require [clojure.java.shell :only [sh]])
  (:gen-class))

(use '[clojure.java.shell :only [sh]])

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
  "Spits a Jira worklog into a jlog.txt file, containing the date, jira issue key, and message."
  [timelog issue message]
  (spit (str (get-jar-root) "jlog.txt") 
        (str (get-date) " --- " issue " *" (space-hrs&mins timelog) "* --- " message "\n")
        :append true)
  (println "Worklog saved successfully!"))

(defn jlog
  "Appends a worklog to a jlog.txt file. This is the jlog main function."
  [arg1 arg2 message]
  (if (= arg1 "-b")
    (if (valid-jlog-time? arg2)
      (spit-log arg2 @(get-jira-issue) message)
      (throw (Exception. "Invalid timelog format provided.")))
    (if (valid-jlog-time? arg1)
      (if (valid-jira-issue? arg2)
        (spit-log arg1 (upper-case arg2) message)
        (throw (Exception. "Invalid Jira issue key format provided.")))
      (throw (Exception. "Invalid timelog format provided."))))
  (shutdown-agents))

(defn print-help
  "Prints the possible jlog commands to the console."
  []
  (println (str "\nValid commands for jlog are:\n\n"
                "   jlog -h                                            -     Prints the help menu to the console.\n"
                "   jlog -o                                            -     Opens your jlog.txt file.\n\n"
                "                       -- OR --\n\n"
                "   jlog -b <timelog> <message in quotes>              -     Writes a worklog to the jlog.txt file, retrieving the issue-key from your branch.\n"
                "   jlog <timelog> <issue key> <message in quotes>     -     Writes a worklog to the jlog.txt file using the provided information.\n")))

(defn -main [& args]
  (cond 
    (= "-h" (first args)) (print-help)
    (= "-o" (first args)) ((sh "open" (str (get-jar-root) "jlog.txt")) (shutdown-agents))
    (= (count (take 3 args)) 3) (apply jlog (take 3 args))
    :else ((println "Syntax error.") (print-help))))
