# clj-lily

A minimal Clojure wrapper around the lilypad music engraving engine.

Generates great looking scores and has midi playback.

This is just a small, hacky proof of concept experiment for now.

Turns an edn file such as 
```
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
          ]}
```
into a lilypad (`.ly`) file such as:
```
\version "2.22.1"
\score {
  <<
    \new Staff {
      \new Voice \relative c'' {
	\set midiInstrument = #"Viola"
	  \key a \minor
	  \clef treble
	  \time 4/4
	  r1 r1 r1 r1 e1 e b b
      }}
  \new Staff {
    \new Voice \relative c {
      \set midiInstrument = #"Cello"
	\key a \minor
	\clef bass
	\time 4/4
	\repeat unfold 2 {a8 c' a f c' a e a}
      \repeat unfold 2 {e, b'' gis f b gis e b'}
      \repeat unfold 2 {a,8 c' a f c' a e a}
      \repeat unfold 2 {e, b'' gis f b gis e b'}
      \repeat unfold 2 {a,8 c' a f c' a e a}
      \repeat unfold 2 {e, b'' gis f b gis e b'}
      \repeat unfold 2 {a,8 c' a f c' a e a}
      \repeat unfold 2 {e, b'' gis f b gis e b'}

    }}
  >>
    \layout { }
  \midi {
    \context {
      \Staff
	\remove Staff_performer
    }
    \context {
      \Voice
	\consists Staff_performer
    }
    \context {
      \Score
	tempoWholesPerMinute = #(ly:make-moment 60 2)
    }}}
```
Which will then be rendered by lilypad into a proper and great looking PDF score.

Uses the javax midi engine to play midi versions of the file.
