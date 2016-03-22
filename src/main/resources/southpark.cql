begin
create (_0:`Person` {`firstname`:"Kyle", `lastname`:"Broflovski", `name`:"Kyle Broflovski"})
create (_5:`Person` {`firstname`:"Butters", `name`:"Butters Stotch"})
create (_16:`Person` {`firstname`:"Eric", `lastname`:"Cartman", `name`:"Eric Cartman"})
create (_17 {`name`:"Stan Marsh"})
create (_18:`Person` {`name`:"Kenny McCormick"})
create (_19:`Person` {`name`:"Wendy Testaburger"})
create (_20:`person` {`name`:"Tweek Tweak"})
create (_21:`Other`:`Character` {`name`:"Mr. Garrison"})
create (_22:`Person`:`Character`:`Significant` {`name`:"Bebe Stevens"})
create _0-[:`KNOWS`]->_16
create _5-[:`KNOWS`]->_0
create _5-[:`KNOWS`]->_16
create _22-[:`FRIEND`]->_19
;
commit