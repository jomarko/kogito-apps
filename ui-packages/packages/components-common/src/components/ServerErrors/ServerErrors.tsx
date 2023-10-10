import React, { useState } from 'react';

import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateBody
} from '@patternfly/react-core/dist/js/components/EmptyState';
import {
  ClipboardCopy,
  ClipboardCopyVariant
} from '@patternfly/react-core/dist/js/components/ClipboardCopy';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { ExclamationCircleIcon } from '@patternfly/react-icons/dist/js/icons/exclamation-circle-icon';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

interface IOwnProps {
  error: any;
  variant: string;
  children?: React.ReactElement;
}
export const ServerErrors: React.FC<IOwnProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const [displayError, setDisplayError] = useState(false);

  const getErrorSubTitle = () => {
    try {
      const errorObject = JSON.parse(props.error);
      if (
        (errorObject.networkError && errorObject.networkError.name) ||
        (errorObject.networkError &&
          !errorObject.networkError.name &&
          errorObject.graphQLErrors &&
          !(errorObject.graphQLErrors.size > 0) &&
          errorObject.message === 'Network error: Failed to fetch')
      ) {
        return 'An error occurred while accessing data. It is possible the data index is still being loaded, please try again in a few moments.';
      } else {
        return 'An error occurred while accessing data.';
      }
    } catch (error) {
      return 'An error occurred while accessing data.';
    }
  };

  const getErrorContent = () => {
    try {
      const errorObject = JSON.parse(props.error);
      return errorObject.networkError && errorObject.networkError.name
        ? JSON.stringify(errorObject.networkError)
        : errorObject.graphQLErrors && errorObject.graphQLErrors.size > 0
        ? JSON.stringify(errorObject.graphQLErrors)
        : JSON.stringify(props.error);
    } catch (error) {
      return props.error;
    }
  };

  const renderContent = () => (
    <Bullseye {...componentOuiaProps(ouiaId, 'server-errors', ouiaSafe)}>
      <EmptyState variant={EmptyStateVariant.full}>
        <EmptyStateIcon
          icon={ExclamationCircleIcon}
          color="var(--pf-global--danger-color--100)"
        />
        <Title headingLevel="h1" size="4xl">
          Error fetching data
        </Title>
        <EmptyStateBody data-testid="empty-state-body">
          {getErrorSubTitle()}{' '}
          <Button
            variant="link"
            isInline
            id="display-error"
            data-testid="display-error"
            onClick={() => setDisplayError(!displayError)}
          >
            See more details
          </Button>
        </EmptyStateBody>
        {displayError && (
          <EmptyStateBody>
            <ClipboardCopy
              isCode
              variant={ClipboardCopyVariant.expansion}
              isExpanded={true}
              className="pf-u-text-align-left"
            >
              {getErrorContent()}
            </ClipboardCopy>
          </EmptyStateBody>
        )}
        {props.children}
      </EmptyState>
    </Bullseye>
  );

  return (
    <>
      {props.variant === 'large' && (
        <PageSection variant="light">{renderContent()}</PageSection>
      )}
      {props.variant === 'small' && renderContent()}
    </>
  );
};
