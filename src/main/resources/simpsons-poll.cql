create (q:Poll:Simpsons{question:"In which town do the Simpsons reside?", options:["Springfield-", "Shelbyville", "Seinfeld"], correctAnswers:[0], multipleAnswers:false})
create (q:Poll:Simpsons{question:"What is the name of the Simpsons' next door neighbor?", options:["Barney Gumble", "Ned Flanders-", "Principal Skinner"], correctAnswers:[1], multipleAnswers:false})
create (q:Poll:Simpsons{question:"Who founded the Simpsons’ town?", options:["Jebadiah Springfield-", "Zachariah Springfield", "Springfield Manhattan"], correctAnswers:[0], multipleAnswers:false})

match (b:Poll) where id(b) =24  create (v:Vote:Simpsons{answers:[1], user: "alex"})-[:ON]->(b)
match (p:Poll),(p1) where id(p) = 24 and id(p1) =25 create (p1)<-[:FOLLOWS]-(p)
match (p:Poll)-[r:FOLLOWS]->(p1:Poll) create (p)-[r1:BEFORE]->(p1)   with r delete r
////

begin
create (_23:`Poll`:`Simpsons` {`correctAnswers`:[0], `multipleAnswers`:false, `options`:["Springfield-", "Shelbyville", "Seinfeld"], `question`:"In which town do the Simpsons reside?"})
create (_24:`Poll`:`Simpsons` {`correctAnswers`:[1], `multipleAnswers`:false, `options`:["Barney Gumble", "Ned Flanders-", "Principal Skinner"], `question`:"What is the name of the Simpsons' next door neighbor?"})
create (_25:`Poll`:`Simpsons` {`correctAnswers`:[0], `multipleAnswers`:false, `options`:["Jebadiah Springfield-", "Zachariah Springfield", "Springfield Manhattan"], `question`:"Who founded the Simpsons’ town?"})
create (_28:`Simpsons`:`Vote` {`answers`:[0], `user`:"alex"})
create (_33:`Simpsons`:`Vote` {`answers`:[0], `user`:"alex"})
create (_34:`Simpsons`:`Vote` {`answers`:[1], `user`:"alex"})
create _23-[:`BEFORE`]->_24
create _24-[:`BEFORE`]->_25
create _28-[:`ON`]->_23
create _33-[:`ON`]->_25
create _34-[:`ON`]->_24
;
commit

match(v:Vote)-[o:ON]-(p:Poll) return v.answers, p.correctAnswers


