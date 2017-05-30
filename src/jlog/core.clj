(ns jlog.core)

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
  "Checks if the argument provided is of format HHhMMm (basic support for JIRA time-log)."
  [timelog]
  (and (has-min-length? timelog 2) (not (nil? (re-find #"^([1-8]h)?([1-6]{0,1}[0-9]{0,1}m)?$" timelog)))))

(defn valid-jlog-args-list?
  "Takes a list of 0, 1, or 2 args and check if this is a valid argument list in the context of jlog."
  [args-list]
  (let [jlog-flag "-t" first-arg (first args-list) second-arg (nth args-list 1)]
    (and (= first-arg jlog-flag) 
         (valid-jlog-time? second-arg))))

(defn -main [& args]
  (println (get-two-args args)))
