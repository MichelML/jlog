(ns jlog.core
  (:require [clojure.string :as str]))

(defn has-length?
  "Checks if a list or string is of length n."
  [args-list n]
  (= (count args-list) n))

(defn has-min-length?
  "Checks if a list or string is of minimum length n."
  [args-list n]
  (>= (count args-list) n))

(defn get-two-args 
  "Takes the args list and return the first two args of the list, or a list of two empty strings if length is less than 2."
  [args-list]
  (if (not (has-min-length? args-list 2)) ["" ""] (take 2 args-list)))

(defn valid-jlog-time?
  "Checks if the argument provided is of format HhMMm (basic support for JIRA time-log)."
  [timelog]
  (and (has-min-length? timelog 2) (not (nil? (re-find #"^([1-8][hH])?([1-6]{0,1}[0-9]{0,1}[mM])?$" timelog)))))

(defn valid-jlog-args-list?
  "Takes a list of 2 args and check if this is a valid argument list in the context of jlog."
  [args-list]
  (let [jlog-flag "-t" first-arg (first args-list) second-arg (nth args-list 1)]
    (and (= first-arg jlog-flag) 
         (valid-jlog-time? second-arg))))

(defn space-hrs&mins
  "Adds a space between the hours log and minutes log if both are present."
  [timelog-raw]
  (let [timelog (str/lower-case timelog-raw)]
    (if (and (str/includes? timelog "h") (str/includes? timelog "m")) 
      (str/replace timelog "h" "h ") 
      timelog)))

(defn -main [& args]
  (if (valid-jlog-args-list? (get-two-args args))
    (spit "jlog.txt" (str (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date)) " --- " (space-hrs&mins (second args)) "\n"))
    (println "Invalid syntax. Run jlog with 'jlog -t 1h'")))
