(ns clj-lily.core
  (:require
    [clojure.java.io    :as io]
    [clojure.java.shell :refer [sh]]
    [clojure.string     :as str]
    [clj-lily.midi      :as midi]
    [clj-lily.parse     :as p]))
	
(defn format-compilation-string
  [string]
  (let [lines (str/split-lines string)]
    (reduce (fn [m line]
              (cond
                (.contains line "warning")
                (update-in m [:warnings] conj line)
                
                (.contains line "error")
                (update-in m [:errors] conj line)

                :else
                (update-in m [:summary] conj line)))
            {:warnings []
             :errors   []
             :summary  []}
            lines)))

(defn render-lilypond
  "Takes a lilypond file and renders the midi and pdf"
  [file-name]
  (let [file (str file-name ".ly")
        process (sh "lilypond" file)
        result  (:err process)
        clean-res (format-compilation-string result)]
    (if (empty? (:errors clean-res)) 
      {:status :ready :summary clean-res}
      {:status :error :summary clean-res})))

(defn process [song file-name]
  (as-> song s
    (p/parse s "")
    (p/adorn s)
    (spit (str file-name ".ly") s))
  (render-lilypond file-name))

(def song
  {:version "2.22.1"
   :score [
           {:new-Staff 
            {:new-Voice--relative-c'
             {:set-midiInstrument "= #\"Celesta\""
              :key "a \\minor"
              :clef "treble"
              :time "4/4"
              :notes [["a b c d g c d a b"]
                      [:repeat-unfold-2 "a4 b c d"]
                      [:repeat-unfold-2 "g,4 a b c"]
                      [:repeat-unfold-2 "f,4 g a b"]
                      [:repeat-unfold-2 "e,4 f gis a"]]}}}
           {:new-Staff 
            {:new-Voice--relative-c,
             {:set-midiInstrument "= #\"Celesta\""
              :key "a \\minor"
              :clef "bass"
              :time "4/4"
              :notes [["g b c d g c d a b"]
                      [:repeat-unfold-2 "e4 b c d"]
                      [:repeat-unfold-2 "e,4 a b c"]
                      [:repeat-unfold-2 "e,4 g a b"]
                      [:repeat-unfold-2 "e,4 f gis a"]]}}}
           ]
   :layout "{ }"
   :midi [
          {:context {:Staff {:remove "Staff_performer"}}}
          {:context {:Voice {:consists "Staff_performer"}}}
          {:context {:Score {:*tempoWholesPerMinute "= #(ly:make-moment 72 1)"}}}
          ]})

(def song2
  {:version "2.22.1"
   :score [
           {:new-Staff 
            {:new-Voice--relative-c''
             {:set-midiInstrument "= #\"Viola\""
              :key "a \\minor"
              :clef "treble"
              :time "4/4"
              :notes [["r1 r1 r1 r1 e1 e b b"]]}}}
           {:new-Staff 
            {:new-Voice--relative-c
             {:set-midiInstrument "= #\"Cello\""
              :key "a \\minor"
              :clef "bass"
              :time "4/4"
              :notes [
                      [:repeat-unfold-2 "a8 c' a f c' a e a"]
                      [:repeat-unfold-2 "e, b'' gis f b gis e b'"]
                      [:repeat-unfold-2 "a,8 c' a f c' a e a"]
                      [:repeat-unfold-2 "e, b'' gis f b gis e b'"]
                      [:repeat-unfold-2 "a,8 c' a f c' a e a"]
                      [:repeat-unfold-2 "e, b'' gis f b gis e b'"]
                      [:repeat-unfold-2 "a,8 c' a f c' a e a"]
                      [:repeat-unfold-2 "e, b'' gis f b gis e b'"]
                      ]}}}
           ]
   :layout "{ }"
   :midi [
          {:context {:Staff {:remove "Staff_performer"}}}
          {:context {:Voice {:consists "Staff_performer"}}}
          {:context {:Score {:*tempoWholesPerMinute "= #(ly:make-moment 60 2)"}}}
          ]})

(comment
  (def file-name "song1")
  (process song2 file-name)
  (midi/play-midi-file file-name)
  (sh "open" "spit-take.pdf")
  (midi/get-available-midi-instruments)
  :end)
