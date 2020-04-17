Grammar:
{S=[S + T, S - T, T], T=[T * F, T / F, F], F=[id]}

input expression:id + id
analysis:
	S	=>	S + T
	S + T	=>	T + T
	T + T	=>	F + T
	F + T	=>	id + T
	id + T	=>	id + F
	id + F	=>	id + id

input expression:id
analysis:
	S	=>	T
	T	=>	F
	F	=>	id

input expression:id+id
analysis:
expression:id+id, analysis failed!

input expression:id * id
analysis:
	S	=>	T
	T	=>	T * F
	T * F	=>	F * F
	F * F	=>	id * F
	id * F	=>	id * id
