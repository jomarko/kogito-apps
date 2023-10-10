import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { EnvelopeBus } from '@kie-tools-core/envelope-bus/dist/api';
import { JobsManagementChannelApi, JobsManagementEnvelopeApi } from '../api';
import { Envelope, EnvelopeDivConfig } from '@kie-tools-core/envelope';
import { JobsManagementEnvelopeContext } from './JobsManagementEnvelopeContext';
import {
  JobsManagementEnvelopeView,
  JobsManagementEnvelopeViewApi
} from './JobsManagementEnvelopeView';
import { JobsManagementEnvelopeApiImpl } from './JobsManagementEnvelopeApiImpl';

/**
 * Function that starts an Envelope application.
 * @param args.config: This passes envelope div config
 * @param args.container: The HTML element in which the Jobs Management View will render
 * @param args.bus: The implementation of a `bus` that knows how to send messages to the Channel.
 *
 */
export function init(args: {
  config: EnvelopeDivConfig;
  container: HTMLElement;
  bus: EnvelopeBus;
}) {
  /**
   * Creates a new generic Envelope, typed with the right interfaces.
   */
  const envelope = new Envelope<
    JobsManagementEnvelopeApi,
    JobsManagementChannelApi,
    JobsManagementEnvelopeViewApi,
    JobsManagementEnvelopeContext
  >(args.bus, args.config);

  const envelopeViewDelegate = async () => {
    const ref = React.createRef<JobsManagementEnvelopeViewApi>();
    return new Promise<() => JobsManagementEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <JobsManagementEnvelopeView
          ref={ref}
          channelApi={envelope.channelApi}
        />,
        args.container,
        () => res(() => ref.current!)
      );
    });
  };

  const context: JobsManagementEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) =>
      new JobsManagementEnvelopeApiImpl(apiFactoryArgs)
  });
}
