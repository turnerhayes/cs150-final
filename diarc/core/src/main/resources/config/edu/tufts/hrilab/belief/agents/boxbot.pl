name(self,boxbot).
name(boxbot,boxbot).

actor(amitis).

diarcAgent(self).
diarcAgent(boxbot).
team(self).

memberOf(X,X).
memberOf(boxbot, self).


object(boxbot, agent).
object(self, agent).

subtype(physical, object).
subtype(switch, physical).
subtype(box, physical).
subtype(agent, physical).

admin(admin).
supervisor(amitis).
admin(amitis).
