import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 1322,
  label: 'tant électorat assez',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'derrière',
};

export const sampleWithPartialData: IEvent = {
  id: 9683,
  label: 'drelin blablabla modeler',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'biathlète',
};

export const sampleWithFullData: IEvent = {
  id: 23703,
  label: 'vraisemblablement du fait que',
  description: 'un peu solliciter solliciter',
  theme: 'apte',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'pacifique accorder terriblement',
  placeDetails: 'horrible dériver',
  adress: 'outre dès d’autant que',
  note: 'vlan',
};

export const sampleWithNewData: NewEvent = {
  label: 'afin que de sorte que',
  dateStart: dayjs('2023-12-17'),
  dateEnd: dayjs('2023-12-18'),
  place: 'de manière à hausser en guise de',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
