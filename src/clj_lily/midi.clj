(ns clj-lily.midi
  (:require [clojure.java.io :as io])
  (:import [javax.sound.midi MidiSystem Sequencer]))
	
(defn get-available-midi-instruments 
  "Returns string names of all availabl midi patches in system" 
  []
  (let [synth (MidiSystem/getSynthesizer)]
    (.open synth)
    (->> synth
         .getAvailableInstruments
         (mapv (fn [i] (.getName i))))))

(defn play-midi-file 
  "Takes a midi file and plays it with built in java midi player"
  [file-name]
  (let [sequencer (MidiSystem/getSequencer)
        file      (str file-name ".midi")]
    (.open sequencer)
    (with-open [is (io/input-stream file)]
      (.setSequence sequencer is)
      (.start sequencer))))
