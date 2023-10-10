import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import ProcessesPage from '../ProcessesPage';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import * as H from 'history';
import { act } from 'react-dom/test-utils';
import * as RuntimeToolsDevUIAppContext from '../../../contexts/DevUIAppContext';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import * as ProcessListGatewayApi from '../../../../channel/ProcessList/ProcessListContext';
jest.mock('../../../containers/ProcessListContainer/ProcessListContainer');
jest.mock(
  '../../../containers/ProcessDefinitionListContainer/ProcessDefinitionListContainer'
);
const MockProcessFormGatewayApi = jest.fn(() => ({
  onOpenProcessListen: jest.fn()
}));
const processListGatewayApi = new MockProcessFormGatewayApi();
jest
  .spyOn(ProcessListGatewayApi, 'useProcessListGatewayApi')
  .mockImplementation(() => processListGatewayApi);
describe('ProcessesPage tests', () => {
  const props = {
    match: {
      params: {
        instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      url: '',
      isExact: false,
      path: ''
    },
    location: H.createLocation(''),
    history: H.createBrowserHistory()
  };

  it('Snapshot - processList page', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <ProcessesPage {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(container.querySelector('h1').textContent).toEqual(
      'Workflow Instances'
    );
  });

  it('Snapshot - processDefinitionList page', async () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Process',
          pluralProcessLabel: 'Processes'
        }}
      >
        <MemoryRouter>
          <ProcessesPage {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const tabs = screen.getAllByRole('tab');

    fireEvent.click(tabs[0]);

    const processListContainer = container.querySelector(
      'section[aria-labelledby="pf-tab-0-process-list-tab"]'
    );

    expect(processListContainer).toBeTruthy();

    fireEvent.click(tabs[1]);

    const processDefinitionContainer = container.querySelector(
      'section[aria-labelledby="pf-tab-0-process-list-tab"]'
    );

    expect(processDefinitionContainer).toBeTruthy();
  });
});
