import React from 'react';
import { act } from 'react-dom/test-utils';
import WorkflowForm, { WorkflowFormProps } from '../WorkflowForm';
import { WorkflowFormDriver } from '../../../../api';
import { fireEvent, render, screen } from '@testing-library/react';
import { MockedWorkflowFormDriver } from '../../../../embedded/tests/mocks/Mocks';
import * as validateWorkflowData from '../validateWorkflowData';

let props: WorkflowFormProps;
let startWorkflowSpy;
const validateWorkflowDataSpy = jest.spyOn(
  validateWorkflowData,
  'validateWorkflowData'
);

const getWorkflowFormDriver = (): WorkflowFormDriver => {
  const driver = new MockedWorkflowFormDriver();
  startWorkflowSpy = jest.spyOn(driver, 'startWorkflow');
  startWorkflowSpy.mockReturnValue(Promise.resolve('newKey'));
  props.driver = driver;
  return driver;
};

const getWorkflowFormWrapper = () => {
  return render(<WorkflowForm {...props} />).container;
};

describe('WorkflowForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      driver: null,
      workflowDefinition: {
        workflowName: 'workflow1',
        endpoint: 'http://localhost:4000/hiring'
      }
    };
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: jest.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // Deprecated
        removeListener: jest.fn(), // Deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn()
      }))
    });
    //HTMLCanvasElement.prototype.getContext = jest.fn();
  });

  it('Workflow Form - rendering', () => {
    const driver = getWorkflowFormDriver();
    validateWorkflowDataSpy.mockReturnValue(true);

    let container;
    act(() => {
      container = getWorkflowFormWrapper();
    });

    expect(container).toMatchSnapshot();

    const workflowForm = container.querySelector('form');
    expect(workflowForm).toBeTruthy();

    act(() => {
      fireEvent.click(screen.getByTestId('start-button'));
    });

    expect(driver.startWorkflow).toHaveBeenCalled();
  });

  it('Workflow Form - validation error', () => {
    const driver = getWorkflowFormDriver();
    validateWorkflowDataSpy.mockReturnValue(false);

    let container;

    act(() => {
      container = getWorkflowFormWrapper();
    });

    const workflowForm = container.querySelector('form');
    expect(workflowForm).toBeTruthy();

    act(() => {
      fireEvent.click(screen.getByTestId('start-button'));
    });
    expect(container).toMatchSnapshot();
    expect(driver.startWorkflow).not.toHaveBeenCalled();
  });

  it('Workflow Form - loading', async () => {
    jest.spyOn(window, 'setTimeout');
    jest.useFakeTimers();

    const driver = new MockedWorkflowFormDriver();
    startWorkflowSpy = jest.spyOn(driver, 'startWorkflow');
    startWorkflowSpy.mockReturnValue(
      new Promise((resolve) => setTimeout(() => resolve(null), 1000))
    );
    props.driver = driver;

    validateWorkflowDataSpy.mockReturnValue(true);

    let container;
    act(() => {
      container = getWorkflowFormWrapper();
    });

    const workflowForm = container.querySelector('form');

    act(() => {
      fireEvent.click(screen.getByTestId('start-button'));
    });

    expect(driver.startWorkflow).toHaveBeenCalled();

    expect(container).toMatchSnapshot();

    await act(async () => {
      Promise.resolve().then(() => jest.advanceTimersByTime(2000));
      new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });
    });

    expect(container).toMatchSnapshot();

    jest.useRealTimers();
  });
});
