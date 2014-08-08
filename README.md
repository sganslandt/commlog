commlog
========

![build-status](https://travis-ci.org/sganslandt/commlog.svg?branch=master)

A simple commlog API, utlizing slf4j to create need comm logs for keeping tracks of requests and responses to/from remote systems.

CommLogImpl.getLog("Name of remote facade") will get you a comm logger that can log your requests and associated responses or errors if that would be the outcome.

A simple example:

    public class RemoteFacade {

        CommLog commLog;

        public RemoteFacade() {
            this.commLog = CommLogImpl.getLog("RemoteFacade");
        }

        public ResponseType doStuff(RequestType request) {
            commLog.request("doStuff", request);

            // do the stuff

            try {
                // do the hard stuff
            } catch(Throwable e) {
                // oops, that request was poorly handled

                commLog.error("Failure when doing the hard stuff", e);
            }

            commLog.response(response);
            return response;
        }
    }

This would print two rows in the log file. One with the request name, the request object stringified and a unique id of the request and one with the response object or exception thrown together with the request name and id so that they can be mapped together in less or your personal favorite viewer tool. If an error is logged, the complete stack trace is put in a second logger that can be kept separate from the comm log to keep it simple and clean.

The request and response objects will by default be stringified with the build in ReflectingPropertyStringer or CollectionStringer.

