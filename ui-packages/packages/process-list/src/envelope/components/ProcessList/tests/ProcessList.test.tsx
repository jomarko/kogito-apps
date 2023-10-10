import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import React from 'react';
import TestProcessListDriver from './mocks/TestProcessListDriver';
import ProcessList from '../ProcessList';
import { processInstances } from './mocks/Mocks';
import { act } from 'react-dom/test-utils';

let driverQueryMock;
let driverApplyFilterMock;
let driverApplySortingMock;

const getProcessListDriver = (items: number): TestProcessListDriver => {
  const driver = new TestProcessListDriver(
    processInstances.slice(0, items),
    []
  );
  jest.spyOn(driver, 'initialLoad');
  driverApplyFilterMock = jest.spyOn(driver, 'applyFilter');
  driverApplySortingMock = jest.spyOn(driver, 'applySorting');
  driverQueryMock = jest.spyOn(driver, 'query');
  props.driver = driver;
  return driver;
};
let props;

const getProcessListWrapper = () => render(<ProcessList {...props} />);
describe('ProcessList test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null
    };
  });

  it('Envelope not connected', async () => {
    const driver = getProcessListDriver(3);

    props.isEnvelopeConnectedToChannel = false;

    const container = getProcessListWrapper();
    expect(container).toMatchSnapshot();
    expect(driver.initialLoad).not.toHaveBeenCalled();
    expect(driver.query).not.toHaveBeenCalled();
    const toolbar = container.getByTestId('data-toolbar-with-filter');
    expect(toolbar).toBeTruthy();
    const Loading = container.getAllByText(/Loading process instances../);
    expect(Loading).toBeTruthy();
  });

  it('ProcessList initialLoad', async () => {
    const driver = getProcessListDriver(3);
    await act(async () => {
      getProcessListWrapper();
    });
    await waitFor(() => screen.getAllByText('Travels'));
    expect(driver.initialLoad).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);
    const toolbar = screen.getByTestId('data-toolbar-with-filter');
    expect(toolbar).toBeTruthy();
    const processListTable = screen.getByTestId('process-list-table');
    expect(processListTable).toBeTruthy();
    expect(processListTable.querySelectorAll('tr').length).toBe(7);
  });

  it('error page', async () => {
    const driver = getProcessListDriver(3);
    driverQueryMock.mockRejectedValue(
      JSON.parse('{"errorMessage":"404 error"}')
    );
    let container;
    await act(async () => {
      container = getProcessListWrapper();
    });
    await waitFor(() => screen.getAllByText('Error fetching data'));
    expect(container).toMatchSnapshot();
  });

  it('apply filter', async () => {
    const driver = getProcessListDriver(3);
    await act(async () => {
      getProcessListWrapper();
    });

    await waitFor(() => screen.getAllByText('Travels'));
    expect(driver.initialLoad).toHaveBeenCalled();
    await act(async () => {
      fireEvent.click(screen.getByTestId('apply-filter-button'));
    });
    expect(driverApplyFilterMock).toHaveBeenCalled();
  });

  it('apply sort', async () => {
    getProcessListDriver(3);
    let container;
    await act(async () => {
      container = getProcessListWrapper().container;
    });
    await waitFor(() => screen.getAllByText('Travels'));
    await act(async () => {
      fireEvent.click(container.querySelector('.pf-c-table__button'));
    });
    expect(driverApplySortingMock).toHaveBeenCalled();
  });

  it('do refresh', async () => {
    const driver = getProcessListDriver(3);
    await act(async () => {
      getProcessListWrapper();
    });
    await waitFor(() => screen.getAllByText('Travels'));
    expect(driver.initialLoad).toHaveBeenCalled();
    await act(async () => {
      fireEvent.click(screen.getByTestId('refresh'));
    });
    expect(driverQueryMock).toHaveBeenCalled();
  });

  it('do reset', async () => {
    const driver = getProcessListDriver(0);
    let container;
    props['initialState'] = {
      filters: {
        status: []
      }
    };
    await act(async () => {
      container = getProcessListWrapper();
    });
    expect(container).toMatchSnapshot();
    await waitFor(() =>
      screen.getAllByText('Try applying at least one filter to see results')
    );
    expect(driver.initialLoad).toHaveBeenCalled();
    await act(async () => {
      fireEvent.click(
        screen.getAllByText('Reset to default', { selector: 'button' })[0]
      );
    });
    expect(driverQueryMock).toHaveBeenCalled();
  });

  it('load more', async () => {
    const driver = getProcessListDriver(10);
    await act(async () => {
      getProcessListWrapper();
    });
    await waitFor(() => screen.getAllByText('Load 10 more'));
    expect(driver.initialLoad).toHaveBeenCalled();
    expect(screen.getAllByText('Load 10 more').length).toBeTruthy();
    await act(async () => {
      fireEvent.click(
        screen.getAllByText('Load 10 more', { selector: 'button' })[0]
      );
    });
    expect(driverQueryMock).toHaveBeenCalled();
  });
});
