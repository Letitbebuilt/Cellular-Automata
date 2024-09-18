XML FORMATTING INSTRUCTIONS
<automata>
	<name></name><!--Display name for automata-->
	<neighbor-type></neighbor-type><!--MOORE and VON-NEUMANN supported, defaults to MOORE-->
	<states><!-- states for the automata -->
		<state>
			<name></name><!--Will display based on case in application-->
			<color></color><!--string or RGB code (rrr,ggg,bbb). Selects random color if fails-->
		</state>
		<state>
			...
		</state>
		<default-state></default-state><!--Should match a name of a state above-->
	</states>
	<transitions><!-- transition rules between states for the automata -->
		<transition>
			<state-from></state-from><!--state coming from (not case sensitive) (can be single state or comma separated list)-->
			<state-to></state-to><!--state going to (not case sensitive)-->
			<!--conditions are a neighbor quantity check, with each additional check being 
			joined via "AND" within a transition-->
			<conditions>
				<condition>
					<state></state><!--state to check (not case sentitive) (can be single state or comma separated list, but only references first one for a quantity check)-->
					<quantity></quantity><!--neighbors matching state (can be single number or comma separated list)-->
					<direction></direction><!--which neighbor to check (LEFT, RIGHT, UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT).-->
				</condition>
			</conditions>
		</transition>
		<transition>
			...
		</transition>	
	</transitions>
</automata>