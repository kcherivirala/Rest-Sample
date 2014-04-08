package com.fbr.domain;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class ResponseList {
    List<Response> responses;

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}

/*

{"responses":[
        {
        "email":"p.sid1@gmail.com",
        "name":"sid1",
        "attributeTuples":[
        {"attributeId":"2acb64e6-a982-4f86-bbcd-0cbdc63a569d",
        "maxValue":5,"obtainedValue":5
        },
        {"attributeId":"50f83a57-db06-4ece-80ed-452bc14c29ee",
        "maxValue":5,"obtainedValue":5
        },
        {"attributeId":"7768fd62-0dd8-4447-8859-821aaeeb527e",
        "maxValue":5,"obtainedValue":5
        }
        ]
        },
        {
        "email":"p.sid2@gmail.com",
        "name":"sid2",
        "attributeTuples":[
        {"attributeId":"2acb64e6-a982-4f86-bbcd-0cbdc63a569d",
        "maxValue":5,"obtainedValue":5
        },
        {"attributeId":"50f83a57-db06-4ece-80ed-452bc14c29ee",
        "maxValue":5,"obtainedValue":5
        },
        {"attributeId":"7768fd62-0dd8-4447-8859-821aaeeb527e",
        "maxValue":5,"obtainedValue":5
        }
        ]
        }
        ]}
*/

