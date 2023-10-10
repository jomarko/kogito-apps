import React from 'react';
import {
  JobStatus,
  Job,
  ProcessInstance,
  NodeInstance
} from '@kogito-apps/management-console-shared/dist/types';
import { setTitle } from '@kogito-apps/management-console-shared/dist/utils/Utils';
import { ClockIcon } from '@patternfly/react-icons/dist/js/icons/clock-icon';
import { BanIcon } from '@patternfly/react-icons/dist/js/icons/ban-icon';
import { UndoIcon } from '@patternfly/react-icons/dist/js/icons/undo-icon';
import { ErrorCircleOIcon } from '@patternfly/react-icons/dist/js/icons/error-circle-o-icon';
import { CheckCircleIcon } from '@patternfly/react-icons/dist/js/icons/check-circle-icon';
import { ProcessDetailsDriver } from '../api';

export const JobsIconCreator = (state: JobStatus): JSX.Element => {
  switch (state) {
    case JobStatus.Error:
      return (
        <>
          <ErrorCircleOIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />
          Error
        </>
      );
    case JobStatus.Canceled:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Canceled
        </>
      );
    case JobStatus.Executed:
      return (
        <>
          <CheckCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--success-color--100)"
          />
          Executed
        </>
      );
    case JobStatus.Retry:
      return (
        <>
          <UndoIcon className="pf-u-mr-sm" />
          Retry
        </>
      );
    case JobStatus.Scheduled:
      return (
        <>
          <ClockIcon className="pf-u-mr-sm" />
          Scheduled
        </>
      );
  }
};

export const handleRetry = async (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleProcessRetry(processInstance);
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
};

export const handleSkip = async (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleProcessSkip(processInstance);
    onSkipSuccess();
  } catch (error) {
    onSkipFailure(JSON.stringify(error.message));
  }
};

export const handleNodeInstanceRetrigger = (
  processInstance: ProcessInstance,
  driver: ProcessDetailsDriver,
  node: NodeInstance,
  onRetriggerSuccess: () => void,
  onRetriggerFailure: (errorMessage: string) => void
) => {
  driver
    .handleNodeInstanceRetrigger(processInstance, node)
    .then(() => {
      onRetriggerSuccess();
    })
    .catch((error) => {
      onRetriggerFailure(JSON.stringify(error.message));
    });
};

export const handleNodeInstanceCancel = (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  node: NodeInstance,
  onCancelSuccess: () => void,
  onCancelFailure: (errorMessage: string) => void
) => {
  drive
    .handleNodeInstanceCancel(processInstance, node)
    .then(() => {
      onCancelSuccess();
    })
    .catch((error) => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const jobCancel = async (
  drive: ProcessDetailsDriver,
  job: Pick<Job, 'id' | 'endpoint'>,
  setModalTitle: (title: JSX.Element) => void,
  setModalContent: (content: string) => void
) => {
  const response = await drive.cancelJob(job);
  setModalTitle(setTitle(response.modalTitle, 'Job cancel'));
  setModalContent(response.modalContent);
};

export const handleJobRescheduleUtil = async (
  repeatInterval,
  repeatLimit,
  scheduleDate,
  selectedJob: Job,
  handleRescheduleAction: () => void,
  driver: ProcessDetailsDriver,
  setRescheduleError: (modalContent: string) => void
): Promise<void> => {
  const response = await driver.rescheduleJob(
    selectedJob,
    repeatInterval,
    repeatLimit,
    scheduleDate
  );
  if (response && response.modalTitle === 'success') {
    handleRescheduleAction();
  } else if (response && response.modalTitle === 'failure') {
    handleRescheduleAction();
    setRescheduleError(response.modalContent);
  }
};
