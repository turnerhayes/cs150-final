%% agents the system should know about
%% actor(matt).
name(self,boxbot).
name(boxbot,boxbot).

diarcAgent(self).
diarcAgent(boxbot).
team(self).

memberOf(X,X).
memberOf(boxbot, self).

object(boxbot, agent).
object(self, agent).

/*rules about who the agent is obliged to listen to */
%% supervisors
%% supervisor(matt).

%% admin
admin(admin).