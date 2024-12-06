name(self,boxbot).
name(boxbot,boxbot).

diarcAgent(self).
diarcAgent(boxbot).
team(self).

memberOf(X,X).
memberOf(boxbot, self).

% not(isHoldingBox(self)).
% not(isInPickupRange(self)).
% not(isSwitchPressed(self)).

object(boxbot, agent).
object(self, agent).

subtype(physical, object).
subtype(switch, physical).
subtype(box, physical).
subtype(agent, physical).

admin(admin).
