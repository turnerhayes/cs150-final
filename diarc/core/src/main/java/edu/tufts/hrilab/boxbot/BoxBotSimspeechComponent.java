package edu.tufts.hrilab.boxbot;

import edu.tufts.hrilab.interfaces.NLUInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADEService;
import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import ai.thinkingrobots.trade.TRADEService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Variable;

import edu.tufts.hrilab.slug.common.UtteranceType;
import edu.tufts.hrilab.slug.parsing.llm.AlternateResponse;
import edu.tufts.hrilab.slug.parsing.llm.Descriptor;
import edu.tufts.hrilab.slug.parsing.llm.Intention;
import edu.tufts.hrilab.slug.parsing.llm.ParserResponse;
import edu.tufts.hrilab.slug.parsing.llm.Proposition;
import edu.tufts.hrilab.slug.parsing.llm.Referent;
import edu.tufts.hrilab.slug.common.Utterance;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class BoxBotSimspeechComponent extends BoxBotComponent implements NLUInterface {
    private static final Logger log = LoggerFactory.getLogger(BoxBotSimspeechComponent.class);

    // SIMSPEECH STUFF
    private String service = null;
   @Override
   protected List<Option> additionalUsageInfo() {
     List<Option> options = new ArrayList<>();
     options.add(Option.builder("service").hasArg().argName("string").desc("TRADE Service to call in place of parseUtterance").build());
     return options;
   }

    @Override
    protected void parseArgs(CommandLine cmdLine) {
        if (cmdLine.hasOption("service")) {
            service = cmdLine.getOptionValue("service");
        }
    }
    /**
     * Parses the input Utterance to obtain semantic information.
     * Optionally use a TRADEService defined in the service property.
     * Default behavior is to use getResponse() to retrieve from parser service.
     *
     * @param input The input Utterance to parse.
     * @return The parsed Utterance with updated semantic information.
     */
    @Override
    public Utterance parseUtterance(Utterance input) {
        String inputString = input.getWordsAsString();
        AlternateResponse altResponse;
        ParserResponse response = null;
        Symbol addressee = null;
        log.info("parseUtterance: " + inputString);
        try {
            altResponse = TRADE.getAvailableService(new TRADEServiceConstraints().name(service).argTypes(String.class))
                    .call(AlternateResponse.class, inputString);
            response = altResponse.response;
            if (altResponse.addressee != null) {
                addressee = altResponse.addressee;
                log.debug("Got addressee from alternate LLM service: " + addressee.toString());
            }
        } catch (TRADEException ex) {
            log.error("Error calling " + service + ".", ex);
        }
        if (response == null) {
            log.error("Error: Failed to get response");
            return null;
        } else {
            log.info("Got response: " + response.toString());
        }
        Utterance.Builder output = new Utterance.Builder(input);
        output.setUtteranceType(UtteranceType.valueOf(response.intention.intent.toUpperCase()));
        // collect referent info by variable name
        Map<String, Referent> referentMap = new HashMap<>();
        List<Variable> variables = new ArrayList<>();
        for (Referent referent : response.referents) {
            referentMap.put(referent.variable_name, referent);
            variables.add(Factory.createVariable(referent.variable_name, referent.type));
        }
        // populate semantics with semantically typed variables
        Proposition prop = response.intention.proposition;
        Predicate semantics;
        if (UtteranceType.valueOf(response.intention.intent.toUpperCase()) == UtteranceType.INSTRUCT) {
            List<Symbol> args = new ArrayList<>();
            // EW MultiLingualPickAndPlaceDemo: Can't do this because of possibility of
            // direct address. Response is supplying the actor
            if (!service.equals("pickAndPlaceLLMParser")) {
                args.add(input.getAddressee());
            } else if (addressee != null) {
                args.add(addressee);
                output.setAddressee(addressee);
            } else {
                args.add(input.getAddressee());
                output.setAddressee(input.getAddressee());
            }
            Arrays.stream(prop.arguments).forEach(arg -> args.add(Factory.createFOL(arg)));
            semantics = Factory.createPredicate(prop.text, args);
        } else {
            semantics = Factory.createPredicate(prop.text, prop.arguments);
        }
        output.setSemantics(semantics.copyWithNewVariableTypes(variables));
        // populate supplemental semantics
        for (Descriptor descriptor : response.descriptors) {
            Predicate descriptorPred = Factory.createPredicate(descriptor.text, descriptor.arguments);
            output.addSupplementalSemantics(descriptorPred.copyWithNewVariableTypes(variables));
        }
        // add tier assignments to supplemental semantics
        variables.forEach(var -> output.addTierAssignment(var,
                Factory.createSymbol(referentMap.get(var.getName()).toString().toUpperCase())));
        // variables.forEach(var -> output.addTierAssignment(var,
        // Factory.createSymbol(referentMap.get(var.getName()).cognitive_status.toUpperCase())));
        return output.build();
    }

    @TRADEService
    public AlternateResponse parseIt(String input) {
        log.info("parseIt(" + input +")");
        ParserResponse pr = new ParserResponse();
        pr.intention = new Intention();
        pr.intention.intent = UtteranceType.INSTRUCT.name();
        pr.intention.proposition = new Proposition();
        pr.intention.proposition.text = input;
        pr.intention.proposition.arguments = new String[0];
        pr.referents = new Referent[0];
        pr.descriptors = new Descriptor[0];
        return new AlternateResponse(pr, new Symbol("boxbot"));
    }
    
}
