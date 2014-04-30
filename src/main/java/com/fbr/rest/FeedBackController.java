package com.fbr.rest;

import com.fbr.domain.Attribute;
import com.fbr.domain.Graph;
import com.fbr.domain.Question;
import com.fbr.domain.ResponseList;
import com.fbr.services.AttributeService;
import com.fbr.services.GraphService;
import com.fbr.services.QuestionService;
import com.fbr.services.ResponseService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;


@Controller
public class FeedBackController {
    private static final Logger logger = Logger.getLogger(FeedBackController.class);
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private GraphService graphService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.POST})
    public ModelAndView addAttribute(@RequestBody Attribute attribute,
                                     HttpServletResponse httpResponse_p, WebRequest request_p) {
        Attribute returnVal;
        try {
            returnVal = attributeService.addAttributeAndValues(attribute);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + returnVal.getAttributeId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }


    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.PUT})
    public ModelAndView updateAttributeAndValues(@PathVariable("attrId") int attrId, @RequestBody Attribute attribute,
                                                 HttpServletResponse httpResponse_p, WebRequest request_p) {
        Attribute dbEntry;
        try {
            dbEntry = attributeService.updateAttributeAndValues(attrId, attribute);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + dbEntry.getAttributeId());
        return new ModelAndView(jsonView_i, DATA_FIELD, dbEntry);
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = RequestMethod.DELETE)
    public ModelAndView deleteAttributeAndValues(@PathVariable("attrId") int attrId,
                                                 HttpServletResponse httpResponse_p) {
        try {
            attributeService.deleteAttributeAndValues(attrId);
        } catch (Exception e) {
            String sMessage = "Error invoking getFunds. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }

        httpResponse_p.setStatus(HttpStatus.OK.value());
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.GET})
    public ModelAndView getAttributeAndValues(@PathVariable("attrId") int attrId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, attributeService.getAttributeAndValues(attrId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.GET})
    public ModelAndView getAttributesAndValues() {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, attributeService.getAttributeAndValues());
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }


    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.POST})
    public ModelAndView addQuestionsAndAnswers(@PathVariable("companyId") int companyId, @RequestBody Question question,
                                               HttpServletResponse httpResponse_p, WebRequest request_p) {
        Question returnVal;
        try {
            returnVal = questionService.addQuestionAndAnswers(companyId, question);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.PUT})
    public ModelAndView updateQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId, @RequestBody Question question,
                                                 HttpServletResponse httpResponse_p, WebRequest request_p) {
        Question returnVal;
        try {
            returnVal = questionService.updateQuestionAndAnswers(companyId, questionId, question);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);

    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = RequestMethod.DELETE)
    public ModelAndView deleteQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId,
                                                 HttpServletResponse httpResponse_p) {
        try {
            questionService.deleteQuestionAndAnswers(companyId, questionId);
        } catch (Exception e) {
            String sMessage = "Error invoking getFunds. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }

        httpResponse_p.setStatus(HttpStatus.OK.value());
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.GET})
    public ModelAndView getQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, questionService.getQuestionAndAnswers(companyId, questionId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.GET})
    public ModelAndView getQuestionsAndAnswers(@PathVariable("companyId") int companyId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, questionService.getQuestionAndAnswers(companyId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }


    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/responses"}, method = {RequestMethod.POST})
    public ModelAndView addResponses(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId,
                                     @RequestBody ResponseList responseList, HttpServletResponse httpResponse_p, WebRequest request_p) {
        try {
            responseService.processResponse(companyId, branchId, responseList.getResponses());
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/responses/company/" + companyId + "/branch/" + branchId);
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }


    @RequestMapping(value = {"/company/{companyId}/aggregate"}, method = {RequestMethod.GET})
    public ModelAndView getAggregate(@PathVariable("companyId") int companyId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, responseService.getAggregateInfo(companyId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }


    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.POST})
    public ModelAndView addGraph(@PathVariable("companyId") int companyId, @RequestBody Graph graph,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) {
        Graph returnVal;
        try {
            returnVal = graphService.addGraph(companyId, graph);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + returnVal.getGraphId());
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.PUT})
    public ModelAndView updateGraph(@PathVariable("graphId") String graphId, @RequestBody Graph graph,
                                    HttpServletResponse httpResponse_p, WebRequest request_p) {
        Graph returnVal;
        try {
            returnVal = graphService.updateGraph(graphId, graph);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + graphId);
        return new ModelAndView(jsonView_i, DATA_FIELD, returnVal);
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.DELETE})
    public ModelAndView deleteGraph(@PathVariable("graphId") String graphId,
                                    HttpServletResponse httpResponse_p) {
        try {
            graphService.deleteGraph(graphId);
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return new ModelAndView(jsonView_i, DATA_FIELD, null);
    }

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.GET})
    public ModelAndView getGraphs(@PathVariable("companyId") int companyId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, graphService.getGraphs(companyId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }

    @RequestMapping(value = {"/graph/{graphId}"}, method = {RequestMethod.GET})
    public ModelAndView getGraph(@PathVariable("graphId") String graphId) {
        try {
            return new ModelAndView(jsonView_i, DATA_FIELD, graphService.getGraph(graphId));
        } catch (Exception e) {
            String sMessage = "Error creating new fund. [%1$s]";
            return createErrorResponse(String.format(sMessage, e.toString()));
        }
    }


    private ModelAndView createErrorResponse(String sMessage) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, sMessage);
    }
}
